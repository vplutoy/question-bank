# 学生管理子系统 — 核心类详细定义

## 一、Entity 层

### 1.1 StudentClass（班级实体类）

| 项目 | 内容 |
| :--- | :--- |
| 包路径 | `com.example.questionbank.entity` |
| 注解 | `@Data`, `@TableName("student_class")` |
| 说明 | 映射 student_class 表，Lombok 自动生成 getter/setter/toString/equals/hashCode |

| 访问修饰符 | 属性名 | 类型 | 注解 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| private | id | Long | `@TableId(type = IdType.AUTO)` | 主键，自增 |
| private | className | String | — | 班级名称 |
| private | department | String | — | 所属院系 |
| private | description | String | — | 班级描述 |
| private | createTime | LocalDateTime | `@TableField(fill = FieldFill.INSERT)` | 创建时间，自动填充 |

### 1.2 Student（学生实体类）

| 项目 | 内容 |
| :--- | :--- |
| 包路径 | `com.example.questionbank.entity` |
| 注解 | `@Data`, `@TableName("student")` |
| 说明 | 映射 student 表 |

| 访问修饰符 | 属性名 | 类型 | 注解 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| private | id | Long | `@TableId(type = IdType.AUTO)` | 主键，自增 |
| private | name | String | — | 学生姓名 |
| private | studentNo | String | — | 学号（唯一键 uk_student_no） |
| private | classId | Long | — | 所属班级ID（FK → student_class.id） |
| private | department | String | — | 所属院系 |
| private | createTime | LocalDateTime | `@TableField(fill = FieldFill.INSERT)` | 创建时间，自动填充 |


## 二、DTO 层（数据传输对象-输入）

### 2.1 StudentClassDTO

| 项目 | 内容 |
| :--- | :--- |
| 包路径 | `com.example.questionbank.dto` |
| 注解 | `@Data` |
| 说明 | 班级创建/编辑时的输入对象 |

| 访问修饰符 | 属性名 | 类型 | 注解 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| private | className | String | `@NotBlank(message = "班级名称不能为空")` | 班级名称（必填） |
| private | department | String | — | 院系（选填） |
| private | description | String | — | 描述（选填） |

### 2.2 StudentDTO

| 项目 | 内容 |
| :--- | :--- |
| 包路径 | `com.example.questionbank.dto` |
| 注解 | `@Data` |
| 说明 | 学生创建/编辑时的输入对象 |

| 访问修饰符 | 属性名 | 类型 | 注解 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| private | name | String | `@NotBlank(message = "姓名不能为空")` | 学生姓名（必填） |
| private | studentNo | String | `@NotBlank(message = "学号不能为空")` | 学号（必填） |
| private | classId | Long | — | 所属班级ID（选填） |
| private | department | String | — | 院系（选填） |


## 三、VO 层（视图对象-输出）

### 3.1 StudentClassVO

| 项目 | 内容 |
| :--- | :--- |
| 包路径 | `com.example.questionbank.dto` |
| 注解 | `@Data` |
| 说明 | 班级列表/详情的输出对象，比 Entity 多一个 studentCount |

| 访问修饰符 | 属性名 | 类型 | 说明 |
| :--- | :--- | :--- | :--- |
| private | id | Long | 班级ID |
| private | className | String | 班级名称 |
| private | department | String | 院系 |
| private | description | String | 描述 |
| private | createTime | LocalDateTime | 创建时间 |
| private | studentCount | Integer | 该班级学生人数（额外统计字段） |

### 3.2 StudentVO

| 项目 | 内容 |
| :--- | :--- |
| 包路径 | `com.example.questionbank.dto` |
| 注解 | `@Data` |
| 说明 | 学生列表/详情的输出对象，比 Entity 多一个 className |

| 访问修饰符 | 属性名 | 类型 | 说明 |
| :--- | :--- | :--- | :--- |
| private | id | Long | 学生ID |
| private | name | String | 姓名 |
| private | studentNo | String | 学号 |
| private | classId | Long | 班级ID |
| private | className | String | 班级名称（JOIN student_class 获取） |
| private | department | String | 院系 |
| private | createTime | LocalDateTime | 创建时间 |

### 3.3 ScoreTrendVO

| 项目 | 内容 |
| :--- | :--- |
| 包路径 | `com.example.questionbank.dto` |
| 注解 | `@Data` |
| 说明 | 成绩趋势分析的数据点，用于 ECharts 折线图 |

| 访问修饰符 | 属性名 | 类型 | 说明 |
| :--- | :--- | :--- | :--- |
| private | examId | Long | 考试ID |
| private | examName | String | 考试名称 |
| private | examTime | LocalDateTime | 考试时间（X轴） |
| private | totalScore | BigDecimal | 学生得分（Y轴） |
| private | paperTotalScore | BigDecimal | 试卷满分（参考线） |

### 3.4 ErrorKnowledgePointVO

| 项目 | 内容 |
| :--- | :--- |
| 包路径 | `com.example.questionbank.dto` |
| 注解 | `@Data` |
| 说明 | 错误知识点统计，用于 ECharts 柱状图 + 表格 |

| 访问修饰符 | 属性名 | 类型 | 说明 |
| :--- | :--- | :--- | :--- |
| private | knowledgePoint | String | 知识点名称 |
| private | errorCount | Long | 出错次数 |
| private | totalCount | Long | 涉及总次数 |
| private | errorRate | BigDecimal | 错误率 = errorCount / totalCount |

### 3.5 StudentAnalysisVO

| 项目 | 内容 |
| :--- | :--- |
| 包路径 | `com.example.questionbank.dto` |
| 注解 | `@Data` |
| 说明 | 班级学情汇总的输出对象，每人一条记录 |

| 访问修饰符 | 属性名 | 类型 | 说明 |
| :--- | :--- | :--- | :--- |
| private | studentNo | String | 学号 |
| private | studentName | String | 姓名 |
| private | className | String | 班级名称 |
| private | department | String | 院系 |
| private | totalExamsTaken | Long | 参加考试次数 |
| private | averageScore | BigDecimal | 平均分 |
| private | highestScore | BigDecimal | 最高分 |
| private | lowestScore | BigDecimal | 最低分 |


## 四、Service 接口层

### 4.1 StudentClassService

| 项目 | 内容 |
| :--- | :--- |
| 包路径 | `com.example.questionbank.service` |
| 类型 | Interface |
| 说明 | 班级管理服务接口 |

| 返回类型 | 方法签名 | 说明 |
| :--- | :--- | :--- |
| `List<StudentClassVO>` | `listAll()` | 查询所有班级，含每班学生人数统计 |
| `StudentClassVO` | `getById(Long id)` | 按ID查询单个班级 |
| `StudentClassVO` | `create(StudentClassDTO dto)` | 创建新班级 |
| `StudentClassVO` | `update(Long id, StudentClassDTO dto)` | 更新班级信息 |
| `void` | `delete(Long id)` | 删除班级（关联学生 class_id→NULL） |

### 4.2 StudentService

| 项目 | 内容 |
| :--- | :--- |
| 包路径 | `com.example.questionbank.service` |
| 类型 | Interface |
| 说明 | 学生管理与学情分析服务接口 |

| 返回类型 | 方法签名 | 说明 |
| :--- | :--- | :--- |
| `Page<StudentVO>` | `pageStudents(Integer page, Integer size, Long classId, String keyword)` | 分页查询学生，支持班级筛选和关键字搜索 |
| `StudentVO` | `getById(Long id)` | 按ID查询学生 |
| `StudentVO` | `getByStudentNo(String studentNo)` | 按学号查询学生 |
| `StudentVO` | `create(StudentDTO dto)` | 创建学生，含学号唯一性校验 |
| `StudentVO` | `update(Long id, StudentDTO dto)` | 更新学生，排除自身的学号唯一性校验 |
| `void` | `delete(Long id)` | 删除学生 |
| `List<ScoreTrendVO>` | `getScoreTrend(String studentNo)` | 查询历次考试成绩趋势 |
| `List<ErrorKnowledgePointVO>` | `getErrorAnalysis(String studentNo)` | 统计错误知识点分布 |
| `List<StudentAnalysisVO>` | `getClassAnalysis(Long classId)` | 汇总班级学情 |


## 五、Service 实现层

### 5.1 StudentClassServiceImpl

| 项目 | 内容 |
| :--- | :--- |
| 包路径 | `com.example.questionbank.service.impl` |
| 注解 | `@Service` |
| 实现 | `implements StudentClassService` |
| 依赖 | StudentClassMapper(读写)、StudentMapper(只读) |

| 访问修饰符 | 方法签名 | 核心逻辑 |
| :--- | :--- | :--- |
| public | `List<StudentClassVO> listAll()` | 查询全部班级 → 遍历用 `studentMapper.selectCount` 统计每班人数 → 组装 VO |
| public | `StudentClassVO getById(Long id)` | 查班级 → null 则抛异常 → 组装 VO |
| public | `StudentClassVO create(StudentClassDTO dto)` | DTO→Entity → `classMapper.insert` → 组装 VO |
| public | `StudentClassVO update(Long id, StudentClassDTO dto)` | 查已有记录 → 更新字段 → `classMapper.updateById` → 组装 VO |
| public | `void delete(Long id)` | 查已有记录 → null 则抛异常 → `classMapper.deleteById` |
| private | `StudentClassVO buildVO(StudentClass sc)` | Entity→VO 映射 + 查询 student 表统计 studentCount |

### 5.2 StudentServiceImpl

| 项目 | 内容 |
| :--- | :--- |
| 包路径 | `com.example.questionbank.service.impl` |
| 注解 | `@Service` |
| 实现 | `implements StudentService` |
| 依赖 | StudentMapper(读写) + StudentClassMapper + 6个只读Mapper |

| 访问修饰符 | 方法签名 | 核心逻辑 |
| :--- | :--- | :--- |
| public | `Page<StudentVO> pageStudents(...)` | LambdaQueryWrapper 构建动态条件（classId eq、keyword like）→ 分页查询 → 逐条 buildVO 填充 className |
| public | `StudentVO getById(Long id)` | 查 Student → 不存在则抛异常 → buildVO |
| public | `StudentVO getByStudentNo(String studentNo)` | `selectOne(eq(student_no))` → null 返回 null |
| public | `StudentVO create(StudentDTO dto)` | **学号唯一校验**: `selectCount(eq(student_no))` > 0 → 抛异常；否则 DTO→Entity → insert → buildVO |
| public | `StudentVO update(Long id, StudentDTO dto)` | **学号唯一校验(排除自身)**: `selectCount(eq(student_no).ne(id))` > 0 → 抛异常；否则更新字段 → updateById → buildVO |
| public | `void delete(Long id)` | 查已有记录 → 不存在则抛异常 → `deleteById` |
| public | `List<ScoreTrendVO> getScoreTrend(String studentNo)` | 查 exam_student_result → 逐条 JOIN exam 获取考试名和时间 → JOIN exam_paper 获取满分 |
| public | `List<ErrorKnowledgePointVO> getErrorAnalysis(String studentNo)` | 查所有 exam_student_answer → 逐条查 question 获取 knowledge_points → split(",") 拆分 → 比对 finalScore vs questionScore 判定对错 → HashMap 累加 errorCount/totalCount → 计算 errorRate → 降序排列 |
| public | `List<StudentAnalysisVO> getClassAnalysis(Long classId)` | 按 classId 查学生列表 → 查全部 exam_student_result 按 student_no 分组 → 逐人计算 totalExamsTaken/average/highest/lowest |
| private | `Student requireStudent(Long id)` | `selectById` → null 则抛异常 "学生不存在" |
| private | `StudentVO buildStudentVO(Student s)` | Entity→VO + `classMapper.selectById` 填充 className |
| private | `BigDecimal getQuestionMaxScore(Long examId, Long questionId)` | 通过 exam→paperId + questionId 查 exam_paper_question 获取每题满分 |


## 六、Controller 层

### 6.1 StudentClassController

| 项目 | 内容 |
| :--- | :--- |
| 包路径 | `com.example.questionbank.controller` |
| 注解 | `@RestController`, `@RequestMapping("/api/student-classes")` |
| 依赖注入 | 构造器注入 `StudentClassService` |

| HTTP方法 | 路径 | 方法签名 | 说明 |
| :--- | :--- | :--- | :--- |
| GET | `/api/student-classes` | `List<StudentClassVO> list()` | 获取全部班级 |
| GET | `/api/student-classes/{id}` | `StudentClassVO getById(@PathVariable Long id)` | 获取单个班级 |
| POST | `/api/student-classes` | `StudentClassVO create(@Valid @RequestBody StudentClassDTO dto)` | 创建班级 |
| PUT | `/api/student-classes/{id}` | `StudentClassVO update(@PathVariable Long id, @Valid @RequestBody StudentClassDTO dto)` | 更新班级 |
| DELETE | `/api/student-classes/{id}` | `Map<String,Object> delete(@PathVariable Long id)` | 删除班级，返回 `{"success":true}` |

### 6.2 StudentController

| 项目 | 内容 |
| :--- | :--- |
| 包路径 | `com.example.questionbank.controller` |
| 注解 | `@RestController`, `@RequestMapping("/api/students")` |
| 依赖注入 | 构造器注入 `StudentService` |

| HTTP方法 | 路径 | 方法签名 | 说明 |
| :--- | :--- | :--- | :--- |
| GET | `/api/students` | `Page<StudentVO> page(Integer page, Integer size, Long classId, String keyword)` | 分页查询 |
| GET | `/api/students/{id}` | `StudentVO getById(@PathVariable Long id)` | 获取学生详情 |
| POST | `/api/students` | `StudentVO create(@Valid @RequestBody StudentDTO dto)` | 创建学生 |
| PUT | `/api/students/{id}` | `StudentVO update(@PathVariable Long id, @Valid @RequestBody StudentDTO dto)` | 更新学生 |
| DELETE | `/api/students/{id}` | `Map<String,Object> delete(@PathVariable Long id)` | 删除学生 |
| GET | `/api/students/analysis` | `List<StudentAnalysisVO> classAnalysis(@RequestParam Long classId)` | 班级学情汇总 |
| GET | `/api/students/{studentNo}/score-trend` | `List<ScoreTrendVO> scoreTrend(@PathVariable String studentNo)` | 成绩趋势 |
| GET | `/api/students/{studentNo}/error-analysis` | `List<ErrorKnowledgePointVO> errorAnalysis(@PathVariable String studentNo)` | 错误知识点分析 |


## 七、Mapper 层

### 7.1 StudentClassMapper

| 项目 | 内容 |
| :--- | :--- |
| 包路径 | `com.example.questionbank.mapper` |
| 注解 | `@Mapper` |
| 继承 | `BaseMapper<StudentClass>` |
| 说明 | MyBatis-Plus 自动提供 CRUD 方法，无需编写 SQL |

### 7.2 StudentMapper

| 项目 | 内容 |
| :--- | :--- |
| 包路径 | `com.example.questionbank.mapper` |
| 注解 | `@Mapper` |
| 继承 | `BaseMapper<Student>` |
| 说明 | MyBatis-Plus 自动提供 CRUD + 分页查询方法 |


## 八、类图关系汇总

```
<<interface>>                   <<interface>>
StudentClassService             StudentService
      △                               △
      | implements                    | implements
      |                               |
StudentClassServiceImpl          StudentServiceImpl
      |                               |
      |-- uses --> StudentClassMapper |-- uses --> StudentMapper
      |-- uses --> StudentMapper      |-- uses --> StudentClassMapper
                                      |-- uses --> ExamStudentResultMapper (R)
                                      |-- uses --> ExamStudentAnswerMapper (R)
                                      |-- uses --> ExamMapper (R)
                                      |-- uses --> ExamPaperMapper (R)
                                      |-- uses --> QuestionMapper (R)
                                      |-- uses --> ExamPaperQuestionMapper (R)

StudentClassController -- uses --> StudentClassService
StudentController      -- uses --> StudentService

(R) = read-only, 跨子系统复用
```
