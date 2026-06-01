<template>
  <div class="login-page">
    <div class="login-card">
      <h1 class="title">题目管理系统</h1>
      <p class="subtitle">请选择教师身份进入系统</p>
      <el-divider />
      <div v-if="loading" class="loading">
        <el-icon class="is-loading" :size="32"><Loading /></el-icon>
      </div>
      <div v-else class="teacher-list">
        <div
          v-for="teacher in teachers"
          :key="teacher.id"
          class="teacher-item"
          @click="handleLogin(teacher)"
        >
          <el-avatar :size="40" icon="UserFilled" />
          <div class="teacher-info">
            <div class="name">{{ teacher.name }}</div>
            <div class="dept">{{ teacher.department }}</div>
          </div>
          <el-icon :size="20"><ArrowRight /></el-icon>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useTeacherStore } from '../stores/teacher'
import { getTeachers } from '../api/teacher'

const router = useRouter()
const store = useTeacherStore()

const teachers = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await getTeachers()
    teachers.value = res.data
  } catch (e) {
    // 网络错误时有 seed 数据兜底
    teachers.value = [
      { id: 1, name: '欧毓毅', department: '计算机学院' },
      { id: 2, name: '张老师', department: '计算机学院' }
    ]
  } finally {
    loading.value = false
  }
})

function handleLogin(teacher) {
  store.login(teacher)
  router.push('/questions')
}
</script>

<style scoped>
.login-page { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.login-card { width: 420px; background: #fff; border-radius: 12px; padding: 40px; box-shadow: 0 20px 60px rgba(0,0,0,0.3); }
.title { text-align: center; font-size: 24px; color: #303133; margin-bottom: 4px; }
.subtitle { text-align: center; font-size: 14px; color: #909399; }
.loading { text-align: center; padding: 40px 0; }
.teacher-item { display: flex; align-items: center; gap: 14px; padding: 14px 12px; border-radius: 8px; cursor: pointer; transition: background 0.2s; }
.teacher-item:hover { background: #f0f2f5; }
.teacher-info { flex: 1; }
.name { font-size: 16px; font-weight: 500; color: #303133; }
.dept { font-size: 13px; color: #909399; margin-top: 2px; }
</style>
