import request from './request'

export function updateQuestionProperties(id, data) {
  return request.put(`/question-properties/${id}`, data)
}
