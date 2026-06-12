# 学生管理子系统 — 各层类文件结构

## 样例图 7-1  Vue 层中包含的类文件

| 文件目录名 | 文件名 | 描述 |
| :--- | :--- | :--- |
| api/ | student.js | 封装学生管理 REST API 调用 |
| views/student/ | StudentClassManage.vue | 班级管理页面（列表+增删改） |
| views/student/ | StudentManage.vue | 学生管理页面（分页+筛选+增删改） |
| views/student/ | StudentAnalysis.vue | 学情分析页面（成绩折线图+错题柱状图） |
| router/ | index.js | 追加 3 条学生管理路由 |
| components/ | AppLayout.vue | 追加「学生管理」侧边栏菜单 |


## 样例图 7-2  Controller 层中包含的类文件

| 文件名 | 描述 |
| :--- | :--- |
| StudentClassController.java | 处理 /api/student-classes 的班级 CRUD 请求 |
| StudentController.java | 处理 /api/students 的学生 CRUD + 学情分析请求 |


## 样例图 7-3  Service 层中包含的类文件

| 文件名 | 描述 |
| :--- | :--- |
| StudentClassService.java | 班级管理服务接口，定义 listAll/getById/create/update/delete 方法 |
| StudentService.java | 学生管理服务接口，定义 CRUD 方法 + 3 个学情分析方法 |
| StudentClassServiceImpl.java | 班级管理服务实现，含学生人数统计逻辑 |
| StudentServiceImpl.java | 学生管理服务实现，含学号唯一校验、错题知识点统计算法、成绩汇总算法 |


## 样例图 7-4  Mapper 层中包含的类文件

| 文件名 | 描述 |
| :--- | :--- |
| StudentClassMapper.java | 班级表数据访问，继承 BaseMapper&lt;StudentClass&gt; |
| StudentMapper.java | 学生表数据访问，继承 BaseMapper&lt;Student&gt; |
| ExamStudentResultMapper.java | 【只读】考生成绩汇总表数据访问 |
| ExamStudentAnswerMapper.java | 【只读】考生答题记录表数据访问 |
| ExamMapper.java | 【只读】考试表数据访问 |
| ExamPaperMapper.java | 【只读】试卷表数据访问 |
| ExamPaperQuestionMapper.java | 【只读】试卷题目关联表数据访问 |
| QuestionMapper.java | 【只读】题目表数据访问 |


## 数据模型层中包含的类文件 (entity + dto)

| 类别 | 文件名 | 描述 |
| :--- | :--- | :--- |
| Entity | StudentClass.java | 班级实体，映射 student_class 表 |
| Entity | Student.java | 学生实体，映射 student 表 |
| DTO | StudentClassDTO.java | 班级输入传输对象 |
| DTO | StudentDTO.java | 学生输入传输对象（含 @NotBlank 校验） |
| VO | StudentClassVO.java | 班级输出视图（含学生人数） |
| VO | StudentVO.java | 学生输出视图（含班级名称） |
| VO | ScoreTrendVO.java | 成绩趋势数据视图 |
| VO | ErrorKnowledgePointVO.java | 错误知识点统计视图 |
| VO | StudentAnalysisVO.java | 班级学情汇总视图 |
