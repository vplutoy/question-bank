import request from './request'

export function createQuestion(data) {
  return request.post('/questions', data)
}

export function updateQuestion(id, data) {
  return request.put(`/questions/${id}`, data)
}

export function deleteQuestion(id) {
  return request.delete(`/questions/${id}`)
}

export function getQuestion(id) {
  return request.get(`/questions/${id}`)
}

export function getQuestions(params) {
  return request.get('/questions', { params })
}
