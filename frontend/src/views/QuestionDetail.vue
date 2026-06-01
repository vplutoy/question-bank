<template>
  <AppLayout>
    <div class="question-detail" v-loading="loading">
      <div class="top-bar">
        <el-button :icon="ArrowLeft" @click="$router.push('/questions')">返回列表</el-button>
        <div>
          <el-button type="primary" @click="$router.push(`/questions/${id}/edit`)">编辑</el-button>
          <el-popconfirm title="确定删除此题目？" @confirm="handleDelete">
            <template #reference>
              <el-button type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </div>
      </div>

      <el-card shadow="never" v-if="question">
        <template #header>
          <div class="card-header">
            <span>#{{ question.id }} {{ question.title }}</span>
            <div class="tags">
              <el-tag :type="typeTag" size="small">{{ typeLabel }}</el-tag>
              <el-tag :type="diffTag" size="small" effect="plain">{{ diffLabel }}</el-tag>
            </div>
          </div>
        </template>

        <el-descriptions :column="2" border>
          <el-descriptions-item label="创建教师">{{ question.teacherName }}</el-descriptions-item>
          <el-descriptions-item label="题型">{{ typeLabel }}</el-descriptions-item>
          <el-descriptions-item label="难度">{{ diffLabel }}</el-descriptions-item>
          <el-descriptions-item label="章节">{{ question.chapter }}</el-descriptions-item>
          <el-descriptions-item label="知识点" :span="2">{{ question.knowledgePoints || '无' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ question.createTime }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ question.updateTime }}</el-descriptions-item>
        </el-descriptions>

        <div class="content-section">
          <h3>题目详情</h3>

          <!-- 选择题 -->
          <template v-if="question.type === 'SINGLE_CHOICE' || question.type === 'MULTIPLE_CHOICE'">
            <div v-for="opt in contentData.options" :key="opt.label" class="option-item">
              <el-tag :type="opt.isCorrect ? 'success' : 'info'" size="small" class="option-label">{{ opt.label }}</el-tag>
              <span class="option-text">{{ opt.text }}</span>
              <el-icon v-if="opt.isCorrect" class="correct-icon" color="#67C23A"><Check /></el-icon>
            </div>
          </template>

          <!-- 填空题 -->
          <template v-if="question.type === 'FILL_BLANK'">
            <div v-for="(blank, idx) in contentData.blanks" :key="idx" class="blank-item">
              <span>第 {{ idx + 1 }} 空：</span>
              <el-tag type="success">{{ blank.answer }}</el-tag>
              <span class="score">{{ blank.score }}分</span>
            </div>
          </template>

          <!-- 主观题 -->
          <template v-if="question.type === 'SUBJECTIVE'">
            <div class="reference-answer">
              <h4>参考答案：</h4>
              <p>{{ contentData.referenceAnswer }}</p>
              <el-tag v-if="contentData.answerImageRequired" type="warning">要求贴图作答</el-tag>
            </div>
          </template>
        </div>

        <div class="attachment-section" v-if="question.attachments && question.attachments.length">
          <h3>题目贴图</h3>
          <div class="image-list">
            <el-image
              v-for="att in question.attachments"
              :key="att.id"
              :src="`/api/files/${att.filePath}`"
              :preview-src-list="[`/api/files/${att.filePath}`]"
              fit="contain"
              style="width:200px;height:200px;margin-right:12px;border-radius:4px"
            />
          </div>
        </div>
      </el-card>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useTeacherStore } from '../stores/teacher'
import AppLayout from '../components/AppLayout.vue'
import { getQuestion, deleteQuestion } from '../api/question'

const route = useRoute()
const router = useRouter()
const store = useTeacherStore()

const id = route.params.id
const question = ref(null)
const loading = ref(false)
const contentData = computed(() => {
  if (!question.value?.contentJson) return {}
  try { return JSON.parse(question.value.contentJson) } catch { return {} }
})

const typeLabel = computed(() => {
  const map = { SINGLE_CHOICE: '单选题', MULTIPLE_CHOICE: '多选题', FILL_BLANK: '填空题', SUBJECTIVE: '主观题' }
  return map[question.value?.type] || ''
})
const typeTag = computed(() => {
  const map = { SINGLE_CHOICE: '', MULTIPLE_CHOICE: 'warning', FILL_BLANK: 'success', SUBJECTIVE: 'danger' }
  return map[question.value?.type] || ''
})
const diffLabel = computed(() => {
  const map = { EASY: '简单', MEDIUM: '中等', HARD: '困难' }
  return map[question.value?.difficulty] || ''
})
const diffTag = computed(() => {
  const map = { EASY: 'success', MEDIUM: 'warning', HARD: 'danger' }
  return map[question.value?.difficulty] || ''
})

onMounted(() => {
  if (!store.isLoggedIn) { router.push('/'); return }
  loadQuestion()
})

function loadQuestion() {
  loading.value = true
  getQuestion(id).then(res => {
    question.value = res.data
  }).finally(() => { loading.value = false })
}

function handleDelete() {
  deleteQuestion(id).then(() => {
    ElMessage.success('删除成功')
    router.push('/questions')
  })
}
</script>

<style scoped>
.question-detail { max-width: 900px; margin: 0 auto; }
.top-bar { display: flex; justify-content: space-between; margin-bottom: 16px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.tags { display: flex; gap: 8px; }
.content-section { margin-top: 20px; }
.content-section h3 { margin-bottom: 12px; color: #303133; }
.option-item { display: flex; align-items: center; gap: 10px; margin-bottom: 8px; padding: 8px 12px; background: #f5f7fa; border-radius: 6px; }
.option-label { min-width: 32px; text-align: center; }
.option-text { flex: 1; }
.correct-icon { flex-shrink: 0; }
.blank-item { margin-bottom: 8px; padding: 8px 12px; background: #f5f7fa; border-radius: 6px; display: flex; align-items: center; gap: 10px; }
.score { color: #909399; font-size: 13px; }
.reference-answer { padding: 12px; background: #f5f7fa; border-radius: 6px; }
.reference-answer h4 { margin-bottom: 8px; }
.reference-answer p { white-space: pre-wrap; color: #606266; }
.attachment-section { margin-top: 20px; }
.attachment-section h3 { margin-bottom: 12px; color: #303133; }
.image-list { display: flex; flex-wrap: wrap; }
</style>
