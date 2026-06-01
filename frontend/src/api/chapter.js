import request from './request'

export function getChapters() { return request.get('/chapters') }
export function createChapter(data) { return request.post('/chapters', data) }
export function updateChapter(id, data) { return request.put(`/chapters/${id}`, data) }
export function deleteChapter(id) { return request.delete(`/chapters/${id}`) }
