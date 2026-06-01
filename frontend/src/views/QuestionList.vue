<template>
  <AppLayout>
    <div class="question-list">
      <div class="search-bar">
        <el-select v-model="filters.type" placeholder="题型" clearable style="width:140px" @change="search">
          <el-option label="单选题" value="SINGLE_CHOICE" />
          <el-option label="多选题" value="MULTIPLE_CHOICE" />
          <el-option label="填空题" value="FILL_BLANK" />
          <el-option label="主观题" value="SUBJECTIVE" />
        </el-select>
        <el-select v-model="filters.difficulty" placeholder="难度" clearable style="width:120px" @change="search">
          <el-option label="简单" value="EASY" />
          <el-option label="中等" value="MEDIUM" />
          <el-option label="困难" value="HARD" />
        </el-select>
        <el-input v-model="filters.keyword" placeholder="搜索题目或知识点" clearable style="width:240px" @clear="search" @keyup.enter="search">
          <template #append>
            <el-button :icon="Search" @click="search" />
          </template>
        </el-input>
        <div class="spacer" />
        <el-button type="primary" :icon="Plus" @click="$router.push('/questions/create')">新建题目</el-button>
      </div>

      <el-table :data="questions" style="width:100%" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="title" label="题目标题" min-width="200" show-overflow-tooltip />
        <el-table-column label="题型" width="100">
          <template #default="{ row }">
            <el-tag :type="typeTag(row.type)" size="small">{{ typeLabel(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="难度" width="90">
          <template #default="{ row }">
            <el-tag :type="diffTag(row.difficulty)" size="small" effect="plain">{{ diffLabel(row.difficulty) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="chapter" label="章节" width="120" show-overflow-tooltip />
        <el-table-column prop="knowledgePoints" label="知识点" min-width="140" show-overflow-tooltip />
        <el-table-column label="创建时间" width="170">
          <template #default="{ row }">{{ row.createTime }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="$router.push(`/questions/${row.id}`)">查看</el-button>
            <el-button link type="primary" size="small" @click="$router.push(`/questions/${row.id}/edit`)">编辑</el-button>
            <el-popconfirm title="确定删除此题目？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button link type="danger" size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="filters.page"
          v-model:page-size="filters.size"
          :total="total"
          :page-sizes="[5,10,20,50]"
          layout="total,sizes,prev,pager,next"
          @size-change="search"
          @current-change="search"
        />
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useTeacherStore } from '../stores/teacher'
import { getQuestions, deleteQuestion } from '../api/question'
import { ElMessage } from 'element-plus'
import AppLayout from '../components/AppLayout.vue'

const router = useRouter()
const store = useTeacherStore()

const questions = ref([])
const total = ref(0)
const loading = ref(false)
const filters = ref({ type: '', difficulty: '', keyword: '', page: 1, size: 10 })

onMounted(() => {
  if (!store.isLoggedIn) { router.push('/'); return }
  search()
})

function search() {
  loading.value = true
  const params = { ...filters.value, teacherId: store.currentTeacher?.id }
  Object.keys(params).forEach(k => { if (!params[k]) delete params[k] })
  getQuestions(params).then(res => {
    questions.value = res.data.records
    total.value = res.data.total
  }).finally(() => { loading.value = false })
}

function handleDelete(id) {
  deleteQuestion(id).then(() => {
    ElMessage.success('删除成功')
    search()
  })
}

function typeLabel(type) {
  const map = { SINGLE_CHOICE: '单选题', MULTIPLE_CHOICE: '多选题', FILL_BLANK: '填空题', SUBJECTIVE: '主观题' }
  return map[type] || type
}
function typeTag(type) {
  const map = { SINGLE_CHOICE: '', MULTIPLE_CHOICE: 'warning', FILL_BLANK: 'success', SUBJECTIVE: 'danger' }
  return map[type] || ''
}
function diffLabel(d) {
  const map = { EASY: '简单', MEDIUM: '中等', HARD: '困难' }
  return map[d] || d
}
function diffTag(d) {
  const map = { EASY: 'success', MEDIUM: 'warning', HARD: 'danger' }
  return map[d] || ''
}
</script>

<style scoped>
.question-list { max-width: 1400px; margin: 0 auto; }
.search-bar { display: flex; gap: 12px; margin-bottom: 16px; align-items: center; }
.spacer { flex: 1; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
