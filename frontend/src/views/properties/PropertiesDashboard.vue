<template>
  <AppLayout>
    <div class="dashboard">
      <h2 class="page-title">题目属性总览</h2>
      <div class="search-bar">
        <el-select v-model="filters.type" placeholder="题型" clearable style="width:130px" @change="search">
          <el-option label="单选题" value="SINGLE_CHOICE" />
          <el-option label="多选题" value="MULTIPLE_CHOICE" />
          <el-option label="填空题" value="FILL_BLANK" />
          <el-option label="主观题" value="SUBJECTIVE" />
        </el-select>
        <el-select v-model="filters.difficulty" placeholder="难度" clearable style="width:110px" @change="search">
          <el-option label="简单" value="EASY" />
          <el-option label="中等" value="MEDIUM" />
          <el-option label="困难" value="HARD" />
        </el-select>
        <el-input v-model="filters.keyword" placeholder="搜索题目" clearable style="width:220px" @clear="search" @keyup.enter="search">
          <template #append><el-button :icon="Search" @click="search" /></template>
        </el-input>
      </div>

      <el-table :data="questions" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
        <el-table-column label="题型" width="90">
          <template #default="{ row }"><el-tag :type="typeTag(row.type)" size="small">{{ typeLabel(row.type) }}</el-tag></template>
        </el-table-column>
        <el-table-column label="难度" width="80">
          <template #default="{ row }"><el-tag :type="diffTag(row.difficulty)" size="small" effect="plain">{{ diffLabel(row.difficulty) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="chapter" label="章节" width="120" show-overflow-tooltip />
        <el-table-column label="出错率" width="100">
          <template #default="{ row }">
            <el-progress :percentage="row.errorRate || 0" :stroke-width="6" :status="row.errorRate > 50 ? 'exception' : (row.errorRate > 30 ? 'warning' : 'success')" />
          </template>
        </el-table-column>
        <el-table-column prop="commonMistakes" label="易错点" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="$router.push(`/properties/question/${row.id}`)">编辑属性</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination v-model:current-page="filters.page" v-model:page-size="filters.size"
          :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next"
          @size-change="search" @current-change="search" />
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useTeacherStore } from '../../stores/teacher'
import { getQuestions } from '../../api/question'
import AppLayout from '../../components/AppLayout.vue'

const router = useRouter()
const store = useTeacherStore()
const questions = ref([])
const total = ref(0)
const loading = ref(false)
const filters = ref({ type: '', difficulty: '', keyword: '', page: 1, size: 10 })

onMounted(() => { if (!store.isLoggedIn) { router.push('/'); return } search() })

function search() {
  loading.value = true
  const params = { ...filters.value, teacherId: store.currentTeacher?.id }
  Object.keys(params).forEach(k => { if (!params[k]) delete params[k] })
  getQuestions(params).then(res => { questions.value = res.data.records; total.value = res.data.total }).finally(() => { loading.value = false })
}

function typeLabel(t) { const m = { SINGLE_CHOICE: '单选题', MULTIPLE_CHOICE: '多选题', FILL_BLANK: '填空题', SUBJECTIVE: '主观题' }; return m[t] || t }
function typeTag(t) { const m = { SINGLE_CHOICE: '', MULTIPLE_CHOICE: 'warning', FILL_BLANK: 'success', SUBJECTIVE: 'danger' }; return m[t] || '' }
function diffLabel(d) { const m = { EASY: '简单', MEDIUM: '中等', HARD: '困难' }; return m[d] || d }
function diffTag(d) { const m = { EASY: 'success', MEDIUM: 'warning', HARD: 'danger' }; return m[d] || '' }
</script>

<style scoped>
.dashboard { max-width: 1400px; margin: 0 auto; }
.page-title { margin-bottom: 16px; font-size: 20px; color: #303133; }
.search-bar { display: flex; gap: 12px; margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
