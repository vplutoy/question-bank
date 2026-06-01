<template>
  <AppLayout>
    <div class="editor-page" v-loading="loading">
      <el-page-header :icon="ArrowLeft" @back="$router.push('/properties')" title="返回总览">
        <template #content>题目属性编辑 - #{{ id }}</template>
      </el-page-header>

      <el-card shadow="never" class="card" v-if="question">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="题目标题" :span="2">{{ question.title }}</el-descriptions-item>
          <el-descriptions-item label="题型">{{ typeLabel(question.type) }}</el-descriptions-item>
          <el-descriptions-item label="创建教师">{{ question.teacherName }}</el-descriptions-item>
        </el-descriptions>

        <el-divider />

        <!-- 答案展示区 -->
        <h3>答案管理</h3>
        <div class="answer-box">
          <template v-if="question.type === 'SINGLE_CHOICE' || question.type === 'MULTIPLE_CHOICE'">
            <div v-for="opt in answerData.options" :key="opt.label" class="opt-row">
              <el-tag :type="opt.isCorrect ? 'success' : 'info'" size="small">{{ opt.label }}</el-tag>
              <span>{{ opt.text }}</span>
            </div>
          </template>
          <template v-else-if="question.type === 'FILL_BLANK'">
            <div v-for="(b, i) in answerData.blanks" :key="i" class="opt-row">
              <span>第{{ i + 1 }}空：</span><el-tag type="success">{{ b.answer }}</el-tag>
            </div>
          </template>
          <template v-else-if="question.type === 'SUBJECTIVE'">
            <p><strong>参考答案：</strong>{{ answerData.referenceAnswer }}</p>
          </template>
          <template v-else>
            <span class="text-muted">无答案数据</span>
          </template>
        </div>

        <el-divider />

        <!-- 题目性质编辑 -->
        <h3>题目性质设置</h3>
        <el-form :model="form" label-width="100px">
          <el-form-item label="难度等级">
            <el-radio-group v-model="form.difficulty">
              <el-radio value="EASY">简单</el-radio>
              <el-radio value="MEDIUM">中等</el-radio>
              <el-radio value="HARD">困难</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="所属章节">
            <el-cascader
              v-model="form.chapterPath"
              :options="chapterTree"
              :props="{ value: 'id', label: 'name', children: 'children', checkStrictly: true, emitPath: false }"
              placeholder="请选择章节"
              clearable
              style="width:100%"
            />
          </el-form-item>

          <el-form-item label="关联知识点">
            <el-select v-model="form.knowledgePointIds" multiple filterable placeholder="选择知识点" style="width:100%">
              <el-option v-for="kp in knowledgePoints" :key="kp.id" :label="kp.name" :value="kp.id" />
            </el-select>
          </el-form-item>

          <el-form-item label="出错率(%)">
            <el-slider v-model="form.errorRate" :min="0" :max="100" show-input style="width:300px" />
          </el-form-item>

          <el-form-item label="易错点">
            <el-input v-model="form.commonMistakes" type="textarea" :rows="3" placeholder="例：学生容易混淆继承和实现的区别" />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="save" :loading="saving">保存设置</el-button>
            <el-button @click="$router.push('/properties')">返回</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useTeacherStore } from '../../stores/teacher'
import AppLayout from '../../components/AppLayout.vue'
import { getQuestion } from '../../api/question'
import { getChapters } from '../../api/chapter'
import { getKnowledgePoints } from '../../api/knowledgePoint'
import { updateQuestionProperties } from '../../api/questionProperties'

const route = useRoute()
const router = useRouter()
const store = useTeacherStore()
const id = route.params.id
const question = ref(null)
const loading = ref(false)
const saving = ref(false)
const knowledgePoints = ref([])
const allChapters = ref([])

const form = ref({
  difficulty: '',
  chapterPath: null,
  chapter: '',
  knowledgePointIds: [],
  errorRate: 0,
  commonMistakes: ''
})

const answerData = computed(() => {
  if (!question.value?.contentJson) return {}
  try { return JSON.parse(question.value.contentJson) } catch { return {} }
})

const chapterTree = computed(() => buildTree(allChapters.value))

onMounted(async () => {
  if (!store.isLoggedIn) { router.push('/'); return }
  loading.value = true
  try {
    const [qRes, chRes, kpRes] = await Promise.all([
      getQuestion(id), getChapters(), getKnowledgePoints()
    ])
    question.value = qRes.data
    allChapters.value = chRes.data
    knowledgePoints.value = kpRes.data
    form.value = {
      difficulty: qRes.data.difficulty,
      chapterPath: null, // cascader matches by current chapter name
      chapter: qRes.data.chapter,
      knowledgePointIds: qRes.data.knowledgePointIds || [],
      errorRate: qRes.data.errorRate || 0,
      commonMistakes: qRes.data.commonMistakes || ''
    }
  } finally { loading.value = false }
})

async function save() {
  saving.value = true
  try {
    await updateQuestionProperties(id, {
      ...form.value,
      chapterPath: undefined
    })
    ElMessage.success('属性保存成功')
  } finally { saving.value = false }
}

function buildTree(list) {
  const map = {}, roots = []
  list.forEach(item => { map[item.id] = { ...item, children: [] } })
  list.forEach(item => {
    if (item.parentId && map[item.parentId]) map[item.parentId].children.push(map[item.id])
    else if (!item.parentId || item.parentId === 0) roots.push(map[item.id])
  })
  return roots
}

function typeLabel(t) { const m = { SINGLE_CHOICE: '单选题', MULTIPLE_CHOICE: '多选题', FILL_BLANK: '填空题', SUBJECTIVE: '主观题' }; return m[t] || t }
</script>

<style scoped>
.editor-page { max-width: 900px; margin: 0 auto; }
.card { margin-top: 16px; }
.answer-box { background: #f5f7fa; border-radius: 8px; padding: 16px; }
.opt-row { display: flex; align-items: center; gap: 10px; margin-bottom: 6px; }
.text-muted { color: #909399; }
h3 { margin: 12px 0; color: #303133; }
</style>
