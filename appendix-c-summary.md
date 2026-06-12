# 附录 C — 实验总结和感想

## 一、系统扩展性和灵活性

### 1.1 子系统解耦设计

学生管理子系统严格遵循"完全解耦"原则：

| 设计决策 | 具体做法 | 效果 |
| :--- | :--- | :--- |
| 只拥有 2 张表 | student_class、student 由本子系统读写，其余 6 张表全部只读复用 | 不修改考试/题目模块任何表结构，子系统独立演进 |
| 隐式关联 | 通过 `student.student_no` 字符串与 `exam_student_result.student_no` 跨子系统关联 | 即使考试模块中学号来自手动输入，学情分析也能查到，零迁移成本 |
| 降级策略 | 错题分析直接解析 `question.knowledge_points` 逗号分隔字段，不依赖 `question_knowledge_point` 关联表 | 即使关联表未同步，分析结果仍然可用 |

### 1.2 分层架构

采用 Spring Boot 标准 5 层架构：

```
Vue 前端 → Controller → Service(接口+实现) → Mapper → MySQL
```

- **Vue 层**：组件化开发，3 个页面组件独立加载，路由懒加载
- **Controller 层**：`@RestController` + 构造器注入，职责单一（接收请求→校验→委托Service→返回JSON）
- **Service 层**：接口驱动设计（Interface/Impl 分离），遵循 DIP 依赖倒置原则，实现类可随时替换
- **Mapper 层**：MyBatis-Plus `BaseMapper<T>` 泛型继承，零 XML 配置，新增实体只需新建接口继承 BaseMapper

### 1.3 数据模型三层分离

| 层次 | 注解 | 职责 | 扩展性 |
| :--- | :--- | :--- | :--- |
| Entity | `@TableName` | 数据库表直接映射 | 新增表→新增 Entity |
| DTO | `@Data` + `@NotBlank` | 前端输入校验 | 新增前端表单→新增 DTO |
| VO | `@Data` | 后端输出视图（可含 JOIN 字段） | 新增展示需求→新增 VO，不影响 Entity |

### 1.4 题型扩展策略（项目全局）

本题库系统已实现 4 种题型（单选/多选/填空/主观题）的策略模式：

- `QuestionTypeStrategy` 接口统一题型行为
- 每种题型一个 Strategy 实现类（`SingleChoiceStrategy`、`MultipleChoiceStrategy`、`FillBlankStrategy`、`SubjectiveStrategy`）
- `QuestionStrategyFactory` 工厂类根据 `QuestionType` 枚举返回对应策略
- 新增题型只需：新建枚举值 + 新建 Strategy 实现类，无需修改已有代码


## 二、AI 工具使用与问题总结

### 2.1 使用的 AI 工具

| 工具 | 用途 |
| :--- | :--- |
| Claude Code (DeepSeek v4 Pro) | 核心开发助手：需求分析、代码生成、UML 图表绘制、文档撰写、问题排查 |
| GitHub Copilot | VS Code 内代码补全、方法生成 |
| draw.io Desktop 30.0.4 | 图表渲染引擎，导出 PNG |
| drawio-skill (Agents365-ai) | draw.io XML 生成规范参考（ERD/UML/时序图预设） |

### 2.2 系统环境

| 项目 | 规格 |
| :--- | :--- |
| 操作系统 | Windows 11 Home China 10.0.26100 |
| CPU | Intel Core i7 |
| 内存 | 16 GB |
| JDK | 17 (Adoptium) |
| Maven | 3.6+ |
| MySQL | 8.0 |
| Node.js | 18+ |
| Vue 3 | 3.4 |
| Spring Boot | 3.2.5 |
| MyBatis-Plus | 3.5.6 |
| draw.io CLI | 30.0.4 |

### 2.3 实验中遇到的问题及解决方法

| 问题 | 原因 | 解决方法 |
| :--- | :--- | :--- |
| **ER 图文字竖排** | `shape=tableRow` 在 Windows 版 draw.io 中存在渲染 bug，`horizontal=0` 导致文字竖直排列；emoji 字符（🔑🔗🟠）触发竖排渲染 | 放弃 tableRow，改用基本形状（rectangle + text）平铺在画布上，去掉所有 emoji 替换为纯 ASCII 文字 |
| **drawio 文件显示空白** | 缺少 `<mxfile>` 和 `<diagram>` 外层包裹——draw.io 标准格式要求，仅有 `<mxGraphModel>` 无法被解析 | 在所有 `.drawio` 文件外层添加 `<mxfile>` + `<diagram>` 包裹 |
| **用例图 export 失败** | `shape=umlActor` 不被 CLI 导出引擎支持，导致 PNG 导出崩溃 | 用基本形状（circle + line × 4）手工绘制火柴人替代 |
| **时序图无文字** | `container=0` 导致 `umlLifeline` 不渲染；`&#xa;` 换行符导致文字消失 | 改为 `container=1`；去掉 `&#xa;` 改用单行标签 |
| **Service 接口类图无文字** | `fontFamily=Courier New` 字体在 Windows 版 draw.io 上渲染失败；`<<` `>>` 尖括号被解析为 XML 导致内容截断 | 去掉自定义字体使用默认；`<<interface>>` 改为 `interface`；`List<X>` 改为 `List~X~` 避免转义 |
| **前后端联调 CORS** | 前端 `localhost:5173` 请求后端 `localhost:8080` 跨域 | vite.config.js 配置 proxy 代理 `/api` 到后端 |
| **学号唯一性校验** | 创建学生时需保证 student_no 唯一，更新时需排除自身 | 使用 MyBatis-Plus `LambdaQueryWrapper.eq(student_no).ne(id)` 实现 |
| **学情分析知识点数据源不可靠** | `question_knowledge_point` 关联表可能未同步 | 直接解析 `question.knowledge_points` 逗号分隔字段作为降级方案 |

### 2.4 实验感想

1. **AI 工具是效率倍增器，但不是银弹**：AI 可以快速生成代码框架、文档表格、UML 图表，但在细节处理（如 drawio 渲染兼容性、业务逻辑边界条件）上仍需人工判断和调试。尤其是跨平台兼容性问题（Windows vs macOS 的 draw.io 行为差异），AI 往往无法预判，需要在真实环境中反复验证。

2. **软件工程原则的价值**：分层架构、接口驱动设计、DTO/VO/Entity 三层模型分离这些经典原则，在 AI 辅助开发中更加重要——清晰的架构让 AI 生成的代码更易集成，减少返工。

3. **图表绘制是 AI 辅助开发的薄弱环节**：本次实验中 ER 图经过了 5+ 轮迭代才得到可用的版本，问题全出在 draw.io 渲染兼容性（emoji、字体、尖括号、container 属性等），而非图的内容设计。这说明当前 AI 对 GUI 工具的细节行为理解不足。

4. **子系统独立开发模式可行**：通过设计阶段约定好接口和数据边界（只读/读写权限、隐式关联字段），5 人小组可以并行开发 8 个子系统而不产生代码冲突。

5. **隐式关联是灵活的双刃剑**：通过 `student_no` 字符串实现跨子系统关联，避免了修改已有表结构，但也带来了外键约束缺失、数据一致性依赖约定等问题，适用于实验场景但在生产环境需谨慎评估。


## 三、完整 AI 提示词记录

> 说明：学生管理子系统是在前 5 个子系统（题目管理、题目性质管理、自动组卷、手动组卷、考试管理）完成并通过 GitHub 协作开发后，从 `git clone` 拉取项目代码开始的。以下提示词为基于实际开发过程的还原记录。


### Phase 1：项目理解与数据库设计

```
1. 你先熟悉这个项目，告诉我整体架构和技术栈
2. 我负责的是 TASK.md 中第6项：学生管理子系统。阅读 Plan-Student.md 理解设计计划
3. 根据 Plan-Student.md 的设计，在 schema.sql 末尾追加 student_class 和 student 两张表的 DDL
4. 帮我执行建表 SQL，并插入一些种子数据用于测试
5. 给出学生管理子系统的 ER 图，包含这2张新表和已有的考试相关表，标注只读/读写关系
```


### Phase 2：后端 Entity + Mapper 层

```
6. 按照项目已有 Entity 的风格（@Data + @TableName + MyBatis-Plus 注解），创建 StudentClass.java 和 Student.java 实体类
7. 创建 StudentClassMapper.java 和 StudentMapper.java，继承 BaseMapper<T>
8. 给 Student 实体加上 @TableField(fill = FieldFill.INSERT) 自动填充 createTime
```


### Phase 3：DTO + VO 数据模型

```
9. 创建 StudentDTO.java 和 StudentClassDTO.java，对必填字段加 @NotBlank 校验
10. 创建 StudentVO.java（含 className 联表字段）和 StudentClassVO.java（含 studentCount 统计字段）
11. 为学情分析创建3个 VO：ScoreTrendVO、ErrorKnowledgePointVO、StudentAnalysisVO
```


### Phase 4：Service 层

```
12. 创建 StudentClassService 接口，定义 listAll/getById/create/update/delete 五个方法
13. 创建 StudentService 接口，定义 CRUD 6个方法 + 学情分析 3个方法（成绩趋势/错题分析/班级汇总）
14. 实现 StudentClassServiceImpl，listAll() 中统计每个班级的学生人数
15. 实现 StudentServiceImpl，create() 和 update() 中校验 student_no 唯一性
16. 实现学情分析的三个方法：getScoreTrend 查考试记录、getErrorAnalysis 解析知识点字段统计错题、getClassAnalysis 按班级汇总成绩指标
```


### Phase 5：Controller 层

```
17. 创建 StudentClassController，映射 /api/student-classes，处理班级 CRUD 请求
18. 创建 StudentController，映射 /api/students，处理学生 CRUD + 3个学情分析请求
19. Controller 中使用 @Valid 校验 DTO，构造器注入 Service
```


### Phase 6：前端页面

```
20. 参照 api/question.js 的风格创建 api/student.js，封装所有学生管理 API 调用
21. 创建 StudentClassManage.vue — el-table 展示班级列表，弹窗表单做增删改
22. 创建 StudentManage.vue — 分页表格（支持班级筛选和关键字搜索），弹窗增删改学生，学号重复提示
23. 创建 StudentAnalysis.vue — el-tabs 切换两个 Tab，ECharts 折线图展示成绩趋势，柱状图+表格展示错题分布
24. 在 router/index.js 追加3条学生管理路由，在 AppLayout.vue 追加"学生管理"菜单
```


### Phase 7：ER 图绘制与调试

```
25. 生成学生管理子系统的 ER 图，包含所有表、字段、关系
26. ER 图的文字重叠在一起，表里行的文本框像竖起来了，怎么回事
27. 把图换成简单方框+文字试试
28. 生成的 ER 图打开是空的，检查文件格式
29. 去掉所有 emoji 符号试试，可能是特殊字符导致的
30. 加上 <mxfile> 外层包裹——原来 draw.io 标准格式要求这个
```


### Phase 8：类图与用例图

```
31. 生成学生管理子系统的 UML 类图，按 Controller/Service/Mapper/Entity 分层排布
32. 生成用例图，只用一个 Actor（教师），13个用例分3个模块组
33. 用例图导出失败，把 umlActor 换成基本形状画火柴人
```


### Phase 9：时序图

```
34. 写出全部13个用例的描述表（编号、参与者、前置条件、基本流程、异常流程）
35. 为 UC-03 创建班级生成时序图，从教师点击到数据库 INSERT 完整链路
36. 为 UC-08 创建学生生成时序图，包含学号唯一性校验的 alt 分支
37. 为 UC-06 生成分页查询时序图，包含动态条件构造和 JOIN 填充 className
38. 为 UC-12 生成错误知识点分析时序图，包含 loop 遍历答题记录和知识点拆分统计算法
39. 为 UC-13 生成班级学情汇总时序图，包含按 student_no 分组计算统计指标
40. 时序图看不到文字——把 container=0 改成 container=1，去掉 &#xa; 换行符
```


### Phase 10：架构图与文件结构

```
41. 画出学生管理子系统的 Spring Boot 分层架构图（Vue→Controller→Service→Mapper→DB）
42. 将架构图的每一层文件分别列表：Vue层、Controller层、Service层、Mapper层
43. 把文件结构表格转成 drawio 方框图
44. 写出所有核心类的完整定义（属性+方法），写入 md 文件
45. 把两个 Service 接口和实现类画成 UML 类图，接口标上方法签名和参数类型
```


### Phase 11：实验报告撰写

```
46. 写出在线学习系统的背景及意义
47. 列出实验所用的硬件环境、软件环境和开发工具
48. 安装 draw.io 桌面版和 drawio-skill 插件
49. 撰写实验总结：扩展性设计、遇到的问题和解决方法、实验感想
50. 把所有生成的文件整理到一个目录，列出文件清单
```
