# 学生管理子系统 — 设计与实现计划

## 一、需求分析

### 1.1 原始需求（TASK.md 第 6 项）
> 学生管理子系统：学生的院系班级分配，学生以班级为单位选课，学生管理，
> 学生情况分析（历次考试成绩波动图，错误知识点统计报告等）。

### 1.2 设计原则
- **完全解耦**：子系统只拥有自己的两张表（`student_class`、`student`），只读已有表（`exam_student_answer`、`exam_student_result`、`question`），不改已有表
- **向后兼容**：通过 `student_no` 字符串与考试模块关联，不修改已有表结构
- **遵循现有模式**：Controller/Service/Mapper/Entity 分层，路径统一 `/api/xxx`
- **可答辩**：接口驱动设计、三层模型分离（Entity/DTO/VO）、分析算法可解释

---

## 二、环境配置

与项目整体一致，无额外配置。需确保 MySQL 中已执行 `schema.sql` 包含 `student_class` 和 `student` 两张新表。

---

## 三、数据库设计

### 3.1 新增表 DDL

```sql
-- 班级表
CREATE TABLE IF NOT EXISTS student_class (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_name VARCHAR(100) NOT NULL COMMENT '班级名称',
    department VARCHAR(100) COMMENT '院系',
    description VARCHAR(500) COMMENT '班级描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';

-- 学生表
CREATE TABLE IF NOT EXISTS student (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '学生姓名',
    student_no VARCHAR(50) NOT NULL COMMENT '学号',
    class_id BIGINT COMMENT '所属班级ID',
    department VARCHAR(100) COMMENT '院系',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_student_no (student_no),
    INDEX idx_class (class_id),
    FOREIGN KEY (class_id) REFERENCES student_class(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生表';
```

### 3.2 设计决策

| 决策 | 理由 |
|------|------|
| `student_no` 作为唯一键 | 与考试模块的 `exam_student_result.student_no` 对齐，实现跨子系统关联 |
| `class_id` 使用 `ON DELETE SET NULL` | 删除班级时保留学生数据，不级联删除 |
| 不建外键到考试表 | 保持子系统解耦；已有考试数据中手动输入的学号也能被分析 |
| `department` 冗余在学生表 | 允许学生与班级院系不同（如转专业），同时避免频繁 join |
| `student_no` 作为分析 API 路径参数 | 与 `ExamManageController` 的 `/results/{studentNo}` 风格一致 |

### 3.3 ER 关系

```
┌─────────────────┐         ┌──────────────────┐
│  student_class  │ 1───*   │     student      │
│─────────────────│         │──────────────────│
│ id (PK)         │         │ id (PK)          │
│ class_name      │         │ name             │
│ department      │         │ student_no (UQ)──┼── 通过 student_no 隐式关联
│ description     │         │ class_id (FK)    │     exam_student_answer
│ create_time     │         │ department       │     exam_student_result
└─────────────────┘         │ create_time      │     question
                            └──────────────────┘
```

---

## 四、后端设计

### 4.1 文件结构

```
backend/src/main/java/com/example/questionbank/
├── entity/
│   ├── StudentClass.java              (新建)
│   └── Student.java                   (新建)
├── mapper/
│   ├── StudentClassMapper.java        (新建)
│   └── StudentMapper.java             (新建)
├── dto/
│   ├── StudentClassDTO.java           (新建)  -- 班级输入
│   ├── StudentClassVO.java            (新建)  -- 班级输出（含 studentCount）
│   ├── StudentDTO.java                (新建)  -- 学生输入
│   ├── StudentVO.java                 (新建)  -- 学生输出（含 className）
│   ├── ScoreTrendVO.java              (新建)  -- 成绩趋势
│   ├── ErrorKnowledgePointVO.java     (新建)  -- 错题知识点统计
│   └── StudentAnalysisVO.java         (新建)  -- 班级分析汇总
├── service/
│   ├── StudentClassService.java       (新建)
│   ├── StudentService.java            (新建)
│   └── impl/
│       ├── StudentClassServiceImpl.java (新建)
│       └── StudentServiceImpl.java      (新建) ← 核心分析逻辑
└── controller/
    ├── StudentClassController.java    (新建)
    └── StudentController.java         (新建)
```

### 4.2 REST API 端点

#### 班级管理 `/api/student-classes`

| Method | Path | 说明 |
|--------|------|------|
| GET | `/api/student-classes` | 获取所有班级（含学生人数） |
| GET | `/api/student-classes/{id}` | 获取班级详情 |
| POST | `/api/student-classes` | 创建班级 |
| PUT | `/api/student-classes/{id}` | 更新班级 |
| DELETE | `/api/student-classes/{id}` | 删除班级 |

#### 学生管理 `/api/students`

| Method | Path | 说明 |
|--------|------|------|
| GET | `/api/students` | 分页查询（classId, keyword 筛选） |
| GET | `/api/students/{id}` | 获取学生详情 |
| POST | `/api/students` | 创建学生（学号唯一校验） |
| PUT | `/api/students/{id}` | 更新学生 |
| DELETE | `/api/students/{id}` | 删除学生 |

#### 学情分析 `/api/students`

| Method | Path | 说明 |
|--------|------|------|
| GET | `/api/students/{studentNo}/score-trend` | 成绩趋势（折线图数据） |
| GET | `/api/students/{studentNo}/error-analysis` | 错误知识点分析（柱状图数据） |
| GET | `/api/students/analysis?classId=X` | 班级维度学情汇总 |

### 4.3 核心分析逻辑（StudentServiceImpl）

#### 分析 1：成绩趋势（getScoreTrend）
```
1. 查询 exam_student_result WHERE student_no = ? ORDER BY submit_time ASC
2. 对每条结果，联查 exam 获取考试名称和开始时间
3. 联查 exam_paper 获取试卷总分
4. 组装 ScoreTrendVO 列表，前端渲染为折线图
   - X 轴：考试名称
   - Y 轴：得分
   - 参考线：满分线
```

#### 分析 2：错误知识点统计（getErrorAnalysis）
```
1. 查询该生所有答题记录 exam_student_answer WHERE student_no = ?
2. 对每条答题：
   a. 获取 question 的 knowledge_points（逗号分隔）
   b. 获取 exam_paper_question 的 question_score（该题满分）
   c. 判定对错：final_score < question_score 则为错
   d. 按逗号拆分知识点，每个知识点：
      - totalCount++（该知识点涉及的总次数）
      - 如果答错：errorCount++
3. 汇总到 Map<String, long[]> (知识点 → [错误次数, 涉及次数])
4. 计算 errorRate = errorCount / totalCount
5. 按错误次数降序排列，返回 ErrorKnowledgePointVO 列表
```

**设计要点**：
- 使用 `question.knowledge_points` 逗号字符串直接解析，不依赖 `question_knowledge_point` 关联表（该表可能未同步）
- 跳过 `final_score` 为 null 的答案（主观题未评分）
- 降序排列方便前端展示 Top N

#### 分析 3：班级学情汇总（getClassAnalysis）
```
1. 按 classId 筛选学生列表（classId 为空时查全部）
2. 查询所有 exam_student_result，按 student_no 分组
3. 对每个学生，计算：
   - totalExamsTaken：参加考试次数
   - averageScore：平均分
   - highestScore：最高分
   - lowestScore：最低分
4. 联查 student_class 填充 className
5. 返回 StudentAnalysisVO 列表
```

### 4.4 依赖关系

```
StudentController
    └── StudentService
         ├── StudentMapper              (读写 student)
         ├── StudentClassMapper         (只读 student_class)
         ├── ExamStudentResultMapper    (只读 exam_student_result)
         ├── ExamStudentAnswerMapper    (只读 exam_student_answer)
         ├── ExamMapper                 (只读 exam)
         ├── ExamPaperMapper            (只读 exam_paper)
         ├── QuestionMapper             (只读 question)
         └── ExamPaperQuestionMapper    (只读 exam_paper_question)

StudentClassController
    └── StudentClassService
         ├── StudentClassMapper         (读写 student_class)
         └── StudentMapper              (只读 student，统计人数)
```

**只读 / 读写关系**：
- 学生模块拥有并读写：`student_class`、`student`
- 学生模块只读：`exam_student_answer`、`exam_student_result`、`exam`、`exam_paper`、`question`、`exam_paper_question`
- **不修改考试模块的任何表**

---

## 五、前端设计

### 5.1 文件结构

```
frontend/src/
├── api/
│   └── student.js                    (新建)
├── views/
│   └── student/
│       ├── StudentClassManage.vue     (新建)  -- 班级 CRUD 表格
│       ├── StudentManage.vue          (新建)  -- 学生分页列表 + 筛选
│       └── StudentAnalysis.vue        (新建)  -- 学情分析（ECharts 图表）
├── router/
│   └── index.js                       (修改)  -- 新增 3 条路由
└── components/
    └── AppLayout.vue                  (修改)  -- 新增"学生管理"菜单
```

### 5.2 StudentClassManage.vue（班级管理）

```
+--------------------------------------------------------------+
| [新建班级]                                                    |
+--------------------------------------------------------------+
|  ID | 班级名称 | 院系 | 描述 | 学生人数 | 创建时间 | 操作      |
|  1  | 软件2001 | 计算机 | ... |   30    | 2025.. | [编辑][删] |
|  2  | 计科2002 | 计算机 | ... |   25    | 2025.. | [编辑][删] |
+--------------------------------------------------------------+
```

弹窗表单：班级名称（必填）、院系、描述（选填）

### 5.3 StudentManage.vue（学生管理）

```
+--------------------------------------------------------------+
| [班级筛选 ▼] [搜索姓名/学号____] [搜索]         [新建学生]      |
+--------------------------------------------------------------+
|  ID | 姓名 | 学号 | 班级 | 院系 | 创建时间 | 操作              |
|  1  | 张三 | 001  | 软1 | 计算机 | ... | [编辑][删除][分析] |
|  2  | 李四 | 002  | 计2 | 计算机 | ... | [编辑][删除][分析] |
+--------------------------------------------------------------+
|                          < 1 2 3 >  共 30 条                  |
+--------------------------------------------------------------+
```

弹窗表单：姓名（必填）、学号（必填+唯一校验）、班级（下拉选择）、院系

### 5.4 StudentAnalysis.vue（学情分析，核心页面）

```
+--------------------------------------------------------------+
| [←返回]  张三 的学情分析    学号：2020001 | 班级：软件工程2001班 |
+--------------------------------------------------------------+
|  [ 成绩趋势 ]  [ 错误知识点分析 ]                               |
+--------------------------------------------------------------+
|                                                               |
|  ┌──────────────────────────────────────────────────────────┐ |
|  │              历次考试成绩趋势（折线图）                     │ |
|  │  100 ┤                    ●                              │ |
|  │   90 ┤      ●─────────────┘                              │ |
|  │   80 ┤      │                                            │ |
|  │      ├──────┼──────────────                              │ |
|  │      │  期中考试    期末考试                               │ |
|  └──────────────────────────────────────────────────────────┘ |
|                                                               |
+--------------------------------------------------------------+

[切换到错误知识点分析 Tab]
+--------------------------------------------------------------+
|                                                               |
|  ┌──────────────────────────────────────────────────────────┐ |
|  │          知识点错误分布 Top 15（水平柱状图）                 │ |
|  │   无穷小与无穷大 ████████████████ 5次                      │ |
|  │   极限四则运算   ██████ 2次                                 │ |
|  │   链式法则       ██ 1次                                    │ |
|  │   函数定义域     ██ 1次                                    │ |
|  └──────────────────────────────────────────────────────────┘ |
|                                                               |
|  | 知识点 | 出错次数 | 涉及次数 | 错误率 |                      |
|  | 无穷小  |   5     |   6     | 83.3%  |  ← 红色标签         |
|  | 极限运算 |   2     |   4     | 50.0%  |  ← 橙色标签         |
+--------------------------------------------------------------+
```

### 5.5 技术选型

| 组件 | 技术 | 说明 |
|------|------|------|
| 图表渲染 | ECharts 5 | `npm install echarts`，在 `onMounted` 中 `echarts.init()` |
| 标签页 | Element Plus `<el-tabs>` | 成绩趋势 / 错误知识点 两个 tab |
| 表格 | Element Plus `<el-table>` | 错题明细表格 |

---

## 六、实施步骤

### Phase 1: 数据库
1. 在 `schema.sql` 末尾追加 `student_class` 和 `student` 的 DDL
2. 执行 SQL 建表

### Phase 2: 后端 Entity + Mapper
3. 创建 `StudentClass.java` 和 `Student.java`
4. 创建 `StudentClassMapper.java` 和 `StudentMapper.java`

### Phase 3: 后端 DTO/VO
5. 创建 `StudentClassDTO.java`、`StudentClassVO.java`
6. 创建 `StudentDTO.java`、`StudentVO.java`
7. 创建 `ScoreTrendVO.java`、`ErrorKnowledgePointVO.java`、`StudentAnalysisVO.java`

### Phase 4: 后端 Service（核心）
8. 创建 `StudentClassService.java` 接口 + `StudentClassServiceImpl.java`
9. 创建 `StudentService.java` 接口 + `StudentServiceImpl.java`（含 3 个分析方法）

### Phase 5: 后端 Controller
10. 创建 `StudentClassController.java`
11. 创建 `StudentController.java`

### Phase 6: 前端 API + 路由
12. 创建 `api/student.js`
13. 修改 `router/index.js` 新增 3 条路由
14. 修改 `AppLayout.vue` 新增"学生管理"菜单组

### Phase 7: 前端页面
15. 安装 echarts：`npm install echarts --save`
16. 创建 `StudentClassManage.vue`
17. 创建 `StudentManage.vue`
18. 创建 `StudentAnalysis.vue`（ECharts 集成）

### Phase 8: 联调测试
19. 端到端测试：创建班级 → 创建学生 → 查看分析
20. 边界测试：无考试记录的学生、全对考生、knowledge_points 为空
21. 数据量测试：班级筛选、分页、Top N 知识点截断

---

## 七、验证方式

### 7.1 后端验证
```bash
cd backend && mvn spring-boot:run

# 测试班级 CRUD
curl http://localhost:8080/api/student-classes
curl -X POST http://localhost:8080/api/student-classes \
  -H "Content-Type: application/json" \
  -d '{"className":"软件工程2001班","department":"计算机学院"}'

# 测试学生 CRUD
curl "http://localhost:8080/api/students?page=1&size=10"
curl -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{"name":"张三","studentNo":"2020001","classId":1,"department":"计算机学院"}'

# 测试学情分析
curl http://localhost:8080/api/students/2020001/score-trend
curl http://localhost:8080/api/students/2020001/error-analysis
curl "http://localhost:8080/api/students/analysis?classId=1"
```

### 7.2 前端验证
```bash
cd frontend && npm run dev
# 访问 http://localhost:5173
# 登录教师 → 学生管理菜单 → 班级管理 → 学生列表 → 学情分析
```

### 7.3 测试用例

| 场景 | 预期结果 |
|------|---------|
| 创建班级 | 列表中显示，学生人数为 0 |
| 创建学生（学号已存在） | 后端返回错误"学号已存在" |
| 删除有学生的班级 | 班级删除成功，学生 class_id 变为 NULL |
| 学分页按班级筛选 | 只显示该班级学生 |
| 学分页按关键字搜索 | 支持姓名和学号模糊匹配 |
| 查看有 2 次考试的学生 | 折线图显示 2 个数据点 |
| 查看有错题的学生 | 柱状图 + 表格显示知识点错误分布 |
| 查看全对的学生 | 错题分析显示"暂无错题记录" |
| 查看无考试的学生 | score-trend 返回空数组，图表区域为空 |
| 班级分析 | 显示每个学生的考试次数、均分、最高分、最低分 |

---

## 八、答辩亮点

| 亮点 | 说明 |
|------|------|
| **完全解耦** | 学生模块独立表结构，不修改考试模块表，通过只读方式复用已有数据 |
| **向后兼容** | 已有考试中手动输入的学号也能被学情分析查询到，零迁移成本 |
| **三层模型分离** | Entity（数据库映射）、DTO（输入校验）、VO（输出视图）职责清晰 |
| **接口驱动设计** | 每个 Service 先定义接口再实现，符合 DIP 依赖倒置原则 |
| **分析算法可解释** | 知识点覆盖率基于逗号分隔字段直接解析，降级方案稳定可靠 |
| **ECharts 可视化** | 折线图展示成绩趋势、柱状图展示错误知识点分布，直观有效 |
| **灵活动态分析** | 支持按班级筛选、按学号追踪，三个分析维度覆盖核心需求 |
| **分页 + 筛选** | 学生列表支持分页、班级筛选、关键字搜索，数据量大也能高效展示 |
