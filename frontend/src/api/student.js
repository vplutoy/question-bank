import request from './request'

// -------- 班级管理 --------
export function getStudentClasses() {
  return request.get('/student-classes')
}
export function getStudentClass(id) {
  return request.get(`/student-classes/${id}`)
}
export function createStudentClass(data) {
  return request.post('/student-classes', data)
}
export function updateStudentClass(id, data) {
  return request.put(`/student-classes/${id}`, data)
}
export function deleteStudentClass(id) {
  return request.delete(`/student-classes/${id}`)
}

// -------- 学生管理 --------
export function getStudents(params) {
  return request.get('/students', { params })
}
export function getStudent(id) {
  return request.get(`/students/${id}`)
}
export function createStudent(data) {
  return request.post('/students', data)
}
export function updateStudent(id, data) {
  return request.put(`/students/${id}`, data)
}
export function deleteStudent(id) {
  return request.delete(`/students/${id}`)
}

// -------- 学情分析 --------
export function getScoreTrend(studentNo) {
  return request.get(`/students/${studentNo}/score-trend`)
}
export function getErrorAnalysis(studentNo) {
  return request.get(`/students/${studentNo}/error-analysis`)
}
export function getStudentAnalysis(classId) {
  return request.get('/students/analysis', { params: { classId } })
}
