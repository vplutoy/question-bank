import request from './request'

export function getKnowledgePoints(chapterId) {
  return request.get('/knowledge-points', { params: chapterId ? { chapterId } : {} })
}
export function createKnowledgePoint(data) { return request.post('/knowledge-points', data) }
export function updateKnowledgePoint(id, data) { return request.put(`/knowledge-points/${id}`, data) }
export function deleteKnowledgePoint(id) { return request.delete(`/knowledge-points/${id}`) }
