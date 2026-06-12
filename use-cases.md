# 学生管理子系统 — 用例描述

## 用例总览

| 编号 | 用例名称 | 参与者 | 所属模块 |
|------|---------|--------|---------|
| UC-01 | 查看班级列表 | 教师 | 班级管理 |
| UC-02 | 查看班级详情 | 教师 | 班级管理 |
| UC-03 | 创建新班级 | 教师 | 班级管理 |
| UC-04 | 编辑班级信息 | 教师 | 班级管理 |
| UC-05 | 删除班级 | 教师 | 班级管理 |
| UC-06 | 分页查询学生列表 | 教师 | 学生管理 |
| UC-07 | 查看学生详情 | 教师 | 学生管理 |
| UC-08 | 创建新学生 | 教师 | 学生管理 |
| UC-09 | 编辑学生信息 | 教师 | 学生管理 |
| UC-10 | 删除学生 | 教师 | 学生管理 |
| UC-11 | 查看学生成绩趋势 | 教师 | 学情分析 |
| UC-12 | 查看错误知识点分析 | 教师 | 学情分析 |
| UC-13 | 查看班级学情汇总 | 教师 | 学情分析 |

---

## 一、班级管理模块

### UC-01：查看班级列表

| 项目 | 内容 |
|------|------|
| **用例名称** | 查看班级列表 |
| **用例编号** | UC-01 |
| **参与者** | 教师（已登录） |
| **前置条件** | 教师已成功登录系统 |
| **后置条件** | 系统展示所有班级及其学生人数 |
| **基本流程** | 1. 教师点击侧边栏「学生管理 → 班级管理」菜单 |
|             | 2. 前端路由导航到班级管理页面 |
|             | 3. 页面 `onMounted` 阶段自动调用 `GET /api/student-classes` |
|             | 4. `StudentClassController.list()` 接收请求 |
|             | 5. 委托 `StudentClassService.listAll()` |
|             | 6. `StudentClassServiceImpl` 查询 `student_class` 表 |
|             | 7. 对每个班级，统计 `student` 表中 `class_id` 匹配的学生人数 |
|             | 8. 组装 `List<StudentClassVO>` 返回前端 |
|             | 9. 前端 `<el-table>` 渲染表格，显示 ID、班级名称、院系、学生人数、操作按钮 |
| **异常流程** | 2a. 如数据库连接失败，返回 HTTP 500，前端提示"加载失败" |

### UC-02：查看班级详情

| 项目 | 内容 |
|------|------|
| **用例名称** | 查看班级详情 |
| **用例编号** | UC-02 |
| **参与者** | 教师（已登录） |
| **前置条件** | 班级列表已加载 |
| **后置条件** | 展示单个班级的完整信息 |
| **基本流程** | 1. 教师在班级列表中点击某班级的「编辑」按钮 |
|             | 2. 前端调用 `GET /api/student-classes/{id}` |
|             | 3. `StudentClassController.getById(id)` 接收请求 |
|             | 4. 委托 `StudentClassService.getById(id)` |
|             | 5. 查询 `student_class` 表，返回 `StudentClassVO` |
|             | 6. 前端弹出编辑对话框，表单回填当前数据 |
| **异常流程** | 2a. 若班级不存在（id 无效），返回 HTTP 404 |

### UC-03：创建新班级

| 项目 | 内容 |
|------|------|
| **用例名称** | 创建新班级 |
| **用例编号** | UC-03 |
| **参与者** | 教师（已登录） |
| **前置条件** | 教师已进入班级管理页面 |
| **后置条件** | 新班级记录写入数据库，列表自动刷新 |
| **基本流程** | 1. 教师点击「新建班级」按钮 |
|             | 2. 前端弹出空白表单（班级名称、院系、描述） |
|             | 3. 教师填写信息并提交 |
|             | 4. 前端构造 `StudentClassDTO`，调用 `POST /api/student-classes` |
|             | 5. `StudentClassController.create(dto)` 接收请求，@Valid 校验 |
|             | 6. 委托 `StudentClassService.create(dto)` |
|             | 7. `StudentClassServiceImpl` 将 DTO 转换为 `StudentClass` 实体 |
|             | 8. 调用 `StudentClassMapper.insert(entity)` 写入数据库 |
|             | 9. 返回 `StudentClassVO`，前端关闭弹窗并刷新列表 |
| **异常流程** | 5a. 班级名称为空 → @Valid 拦截，返回 400 "班级名称不能为空" |
|             | 8a. 数据库写入失败 → 返回 HTTP 500 |

### UC-04：编辑班级信息

| 项目 | 内容 |
|------|------|
| **用例名称** | 编辑班级信息 |
| **用例编号** | UC-04 |
| **参与者** | 教师（已登录） |
| **前置条件** | 目标班级已存在 |
| **后置条件** | 班级信息更新，列表刷新 |
| **基本流程** | 1. 教师在班级列表中点击某班级的「编辑」按钮 |
|             | 2. 系统加载该班级当前数据（同 UC-02），回填表单 |
|             | 3. 教师修改信息后提交 |
|             | 4. 前端调用 `PUT /api/student-classes/{id}`，传入 `StudentClassDTO` |
|             | 5. `StudentClassController.update(id, dto)` 接收请求 |
|             | 6. 委托 `StudentClassService.update(id, dto)` |
|             | 7. `StudentClassServiceImpl` 先查询已有记录，再更新字段 |
|             | 8. 调用 `StudentClassMapper.updateById(entity)` |
|             | 9. 返回更新后的 `StudentClassVO` |
| **异常流程** | 7a. 班级不存在 → 返回 HTTP 404 |

### UC-05：删除班级

| 项目 | 内容 |
|------|------|
| **用例名称** | 删除班级 |
| **用例编号** | UC-05 |
| **参与者** | 教师（已登录） |
| **前置条件** | 目标班级已存在 |
| **后置条件** | 班级记录删除，关联学生的 class_id 置为 NULL（ON DELETE SET NULL） |
| **基本流程** | 1. 教师在班级列表中点击某班级的「删除」按钮 |
|             | 2. 前端弹出确认对话框："确定删除该班级？" |
|             | 3. 教师确认后，前端调用 `DELETE /api/student-classes/{id}` |
|             | 4. `StudentClassController.delete(id)` 接收请求 |
|             | 5. 委托 `StudentClassService.delete(id)` |
|             | 6. `StudentClassServiceImpl` 调用 `StudentClassMapper.deleteById(id)` |
|             | 7. 数据库删除记录；关联学生 `class_id` 自动置 NULL |
|             | 8. 返回 `{"success": true}`，前端刷新列表 |
| **异常流程** | 6a. 班级不存在 → 返回 HTTP 404 |
|             | 3a. 教师取消 → 无操作 |

---

## 二、学生管理模块

### UC-06：分页查询学生列表

| 项目 | 内容 |
|------|------|
| **用例名称** | 分页查询学生列表 |
| **用例编号** | UC-06 |
| **参与者** | 教师（已登录） |
| **前置条件** | 教师已进入学生管理页面 |
| **后置条件** | 系统展示分页的学生列表 |
| **基本流程** | 1. 教师点击侧边栏「学生管理 → 学生列表」菜单 |
|             | 2. 页面 `onMounted` 调用 `GET /api/students?page=1&size=10` |
|             | 3. `StudentController.page(page, size, classId, keyword)` 接收请求 |
|             | 4. 委托 `StudentService.pageStudents(page, size, classId, keyword)` |
|             | 5. `StudentServiceImpl` 构造 MyBatis-Plus 分页查询 |
|             | 6. 如有 `classId` 筛选，添加 `eq("class_id", classId)` 条件 |
|             | 7. 如有 `keyword` 筛选，添加 `like("name", keyword) OR like("student_no", keyword)` |
|             | 8. 查询 `student` 表，联查 `student_class` 填充 `className` |
|             | 9. 返回 `Page<StudentVO>`，包含分页信息（total, pages, current） |
|             | 10. 前端渲染表格和分页控件 |
| **异常流程** | 3a. 参数非法（page<1 或 size<1）→ 使用默认值 page=1, size=10 |

### UC-07：查看学生详情

| 项目 | 内容 |
|------|------|
| **用例名称** | 查看学生详情 |
| **用例编号** | UC-07 |
| **参与者** | 教师（已登录） |
| **前置条件** | 学生列表已加载 |
| **后置条件** | 展示学生完整信息（含所属班级名称） |
| **基本流程** | 1. 教师在学生列表中点击某学生的「编辑」按钮 |
|             | 2. 前端调用 `GET /api/students/{id}` |
|             | 3. `StudentController.getById(id)` 接收请求 |
|             | 4. 委托 `StudentService.getById(id)` |
|             | 5. `StudentServiceImpl` 查询 `student` 表并 JOIN `student_class` |
|             | 6. 组装 `StudentVO`（含 className）返回 |
|             | 7. 前端弹出编辑对话框，回填数据 |
| **异常流程** | 2a. 学生不存在 → 返回 HTTP 404 |

### UC-08：创建新学生

| 项目 | 内容 |
|------|------|
| **用例名称** | 创建新学生 |
| **用例编号** | UC-08 |
| **参与者** | 教师（已登录） |
| **前置条件** | 教师已进入学生管理页面 |
| **后置条件** | 新学生记录写入数据库，学号唯一校验通过 |
| **基本流程** | 1. 教师点击「新建学生」按钮 |
|             | 2. 前端弹出表单（姓名、学号、班级下拉、院系） |
|             | 3. 教师填写并提交 |
|             | 4. 前端构造 `StudentDTO`，调用 `POST /api/students` |
|             | 5. `StudentController.create(dto)` 接收请求，@Valid 校验 |
|             | 6. 委托 `StudentService.create(dto)` |
|             | 7. `StudentServiceImpl` 先检查 `student_no` 是否已存在 |
|             | 8. 若唯一，将 DTO 转为 `Student` 实体 |
|             | 9. 调用 `StudentMapper.insert(entity)` 写入数据库 |
|             | 10. 返回 `StudentVO`（含 className），前端刷新列表 |
| **异常流程** | 7a. 学号已存在 → 返回 HTTP 400 "学号已存在" |
|             | 5a. 姓名或学号为空 → 返回 HTTP 400 "@NotBlank 校验失败" |

### UC-09：编辑学生信息

| 项目 | 内容 |
|------|------|
| **用例名称** | 编辑学生信息 |
| **用例编号** | UC-09 |
| **参与者** | 教师（已登录） |
| **前置条件** | 目标学生已存在 |
| **后置条件** | 学生信息更新 |
| **基本流程** | 1. 教师点击某学生的「编辑」按钮 |
|             | 2. 系统加载该学生当前数据回填表单（同 UC-07） |
|             | 3. 教师修改后提交 |
|             | 4. 前端调用 `PUT /api/students/{id}`，传入 `StudentDTO` |
|             | 5. `StudentController.update(id, dto)` 接收请求 |
|             | 6. 委托 `StudentService.update(id, dto)` |
|             | 7. `StudentServiceImpl` 校验学号唯一性（排除自身） |
|             | 8. 更新实体字段，调用 `StudentMapper.updateById(entity)` |
|             | 9. 返回更新后的 `StudentVO` |
| **异常流程** | 7a. 新学号与他人冲突 → 返回 HTTP 400 |

### UC-10：删除学生

| 项目 | 内容 |
|------|------|
| **用例名称** | 删除学生 |
| **用例编号** | UC-10 |
| **参与者** | 教师（已登录） |
| **前置条件** | 目标学生已存在 |
| **后置条件** | 学生记录被删除 |
| **基本流程** | 1. 教师点击某学生的「删除」按钮 |
|             | 2. 前端弹出确认对话框 |
|             | 3. 教师确认，调用 `DELETE /api/students/{id}` |
|             | 4. `StudentController.delete(id)` 接收请求 |
|             | 5. 委托 `StudentService.delete(id)` |
|             | 6. `StudentServiceImpl` 调用 `StudentMapper.deleteById(id)` |
|             | 7. 返回 `{"success": true}`，前端刷新列表 |
| **异常流程** | 6a. 学生不存在 → 返回 HTTP 404 |

---

## 三、学情分析模块

### UC-11：查看学生成绩趋势

| 项目 | 内容 |
|------|------|
| **用例名称** | 查看学生成绩趋势 |
| **用例编号** | UC-11 |
| **参与者** | 教师（已登录） |
| **前置条件** | 目标学生在系统中存在，且有考试记录 |
| **后置条件** | 前端以折线图展示该生历次考试成绩变化 |
| **基本流程** | 1. 教师在学生列表中点击某学生的「分析」按钮 |
|             | 2. 前端路由跳转到 `/students/analysis/{studentNo}` |
|             | 3. 页面自动调用 `GET /api/students/{studentNo}/score-trend` |
|             | 4. `StudentController.scoreTrend(studentNo)` 接收请求 |
|             | 5. 委托 `StudentService.getScoreTrend(studentNo)` |
|             | 6. `StudentServiceImpl` 查询 `exam_student_result` 表（按 student_no） |
|             | 7. JOIN `exam` 获取考试名称和时间 |
|             | 8. JOIN `exam_paper` 获取试卷总分 |
|             | 9. 组装 `List<ScoreTrendVO>`：{ examId, examName, examTime, totalScore(得分), paperTotalScore(满分) } |
|             | 10. 前端用 ECharts 渲染折线图：X 轴=考试名称，Y 轴=分数，参考线=满分 |
| **异常流程** | 6a. 该生无考试记录 → 返回空数组，图表区域显示"暂无考试记录" |

### UC-12：查看错误知识点分析

| 项目 | 内容 |
|------|------|
| **用例名称** | 查看错误知识点分析 |
| **用例编号** | UC-12 |
| **参与者** | 教师（已登录） |
| **前置条件** | 目标学生存在且有答题记录 |
| **后置条件** | 前端展示该生各知识点的错误次数和错误率（柱状图+表格） |
| **基本流程** | 1. 教师在学情分析页面切换到「错误知识点分析」Tab |
|             | 2. 前端调用 `GET /api/students/{studentNo}/error-analysis` |
|             | 3. `StudentController.errorAnalysis(studentNo)` 接收请求 |
|             | 4. 委托 `StudentService.getErrorAnalysis(studentNo)` |
|             | 5. `StudentServiceImpl` 查询该生所有 `exam_student_answer` 记录 |
|             | 6. 对每条答题记录： |
|             |    a. 获取对应 `question` 的 `knowledge_points`（逗号分隔） |
|             |    b. 获取 `exam_paper_question.question_score`（该题满分） |
|             |    c. 比较 `final_score` 与 `question_score` 判定对错 |
|             |    d. 对每个知识点累加 `totalCount` 和 `errorCount` |
|             | 7. 汇总到 `Map<String, long[]>`，计算 `errorRate = errorCount/totalCount` |
|             | 8. 按 `errorCount` 降序排列，组装 `List<ErrorKnowledgePointVO>` |
|             | 9. 前端渲染水平柱状图（Top 15）+ 明细表格 |
| **异常流程** | 6a. 该生题目 `final_score` 为 null（主观题未评分）→ 跳过该题 |
|             | 6b. `knowledge_points` 字段为空 → 跳过该题 |
|             | 5a. 该生无答题记录 → 返回空数组，显示"暂无错题记录" |

### UC-13：查看班级学情汇总

| 项目 | 内容 |
|------|------|
| **用例名称** | 查看班级学情汇总 |
| **用例编号** | UC-13 |
| **参与者** | 教师（已登录） |
| **前置条件** | 系统中存在学生和考试记录 |
| **后置条件** | 前端展示每个学生的考试次数、平均分、最高分、最低分 |
| **基本流程** | 1. 教师在学生管理页面选择班级筛选条件，点击「班级分析」 |
|             | 2. 前端调用 `GET /api/students/analysis?classId=X`（classId 为空查全部） |
|             | 3. `StudentController.classAnalysis(classId)` 接收请求 |
|             | 4. 委托 `StudentService.getClassAnalysis(classId)` |
|             | 5. `StudentServiceImpl` 按 classId 筛选学生列表 |
|             | 6. 查询所有 `exam_student_result`，按 `student_no` 分组 |
|             | 7. 对每个学生计算： |
|             |    a. `totalExamsTaken`：参加考试次数 |
|             |    b. `averageScore`：平均分 |
|             |    c. `highestScore`：最高分 |
|             |    d. `lowestScore`：最低分 |
|             | 8. 联查 `student_class` 填充 `className` |
|             | 9. 组装 `List<StudentAnalysisVO>` 返回前端 |
|             | 10. 前端以表格展示 |
| **异常流程** | 6a. 该班级无考试记录 → 返回列表各项指标为 0/空 |
