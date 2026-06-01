import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useTeacherStore = defineStore('teacher', () => {
  const currentTeacher = ref(JSON.parse(localStorage.getItem('currentTeacher') || 'null'))

  const isLoggedIn = computed(() => currentTeacher.value !== null)

  function login(teacher) {
    currentTeacher.value = teacher
    localStorage.setItem('currentTeacher', JSON.stringify(teacher))
  }

  function logout() {
    currentTeacher.value = null
    localStorage.removeItem('currentTeacher')
  }

  return { currentTeacher, isLoggedIn, login, logout }
})
