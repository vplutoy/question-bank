import request from './request'

export function getTeachers() {
  return request.get('/teachers')
}
