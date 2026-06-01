<template>
  <AppLayout>
    <div class="question-form">
      <h2 class="page-title">{{ isEdit ? '编辑题目' : '新建题目' }}</h2>
      <el-card shadow="never">
        <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" v-loading="loading">
          <el-form-item label="题型" prop="type">
            <el-select v-model="form.type" placeholder="请选择题型" @change="onTypeChange">
              <el-option label="单选题" value="SINGLE_CHOICE" />
              <el-option label="多选题" value="MULTIPLE_CHOICE" />
              <el-option label="填空题" value="FILL_BLANK" />
              <el-option label="主观题（贴图作答）" value="SUBJECTIVE" />
            </el-select>
          </el-form-item>

          <el-form-item label="题目标题" prop="title">
            <el-input v-model="form.title" placeholder="请输入题目标题" />
          </el-form-item>

          <el-form-item label="难度" prop="difficulty">
            <el-radio-group v-model="form.difficulty">
              <el-radio value="EASY">简单</el-radio>
              <el-radio value="MEDIUM">中等</el-radio>
              <el-radio value="HARD">困难</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="章节" prop="chapter">
            <el-input v-model="form.chapter" placeholder="如：第一章 面向对象概述" />
          </el-form-item>

          <el-form-item label="知识点">
            <el-input v-model="form.knowledgePoints" placeholder="多个知识点用逗号分隔，如：封装,继承,多态" />
          </el-form-item>

          <!-- 题型专属编辑器 -->
          <component :is="editorComponent" v-model="form.contentJson" />

          <el-form-item label="题目贴图">
            <ImageUploader v-model="attachmentIds" />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" size="large" @click="handleSubmit" :loading="submitting">
              {{ isEdit ? '保存修改' : '创建题目' }}
            </el-button>
            <el-button size="large" @click="$router.push('/questions')">取消</el-button>
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
import { useTeacherStore } from '../stores/teacher'
import AppLayout from '../components/AppLayout.vue'
import ImageUploader from '../components/ImageUploader.vue'
import SingleChoiceEditor from '../components/SingleChoiceEditor.vue'
import MultipleChoiceEditor from '../components/MultipleChoiceEditor.vue'
import FillBlankEditor from '../components/FillBlankEditor.vue'
import SubjectiveEditor from '../components/SubjectiveEditor.vue'
import { createQuestion, updateQuestion, getQuestion } from '../api/question'

const route = useRoute()
const router = useRouter()
const store = useTeacherStore()

const isEdit = computed(() => !!route.params.id)
const formRef = ref(null)
const submitting = ref(false)
const loading = ref(false)
const attachmentIds = ref([])

const form = ref({
  type: 'SINGLE_CHOICE',
  title: '',
  contentJson: '',
  difficulty: '',
  chapter: '',
  knowledgePoints: ''
})

const rules = {
  type: [{ required: true, message: '请选择题型', trigger: 'change' }],
  title: [{ required: true, message: '请输入题目标题', trigger: 'blur' }],
  difficulty: [{ required: true, message: '请选择难度', trigger: 'change' }],
  chapter: [{ required: true, message: '请输入章节', trigger: 'blur' }]
}

const editorMap = {
  SINGLE_CHOICE: SingleChoiceEditor,
  MULTIPLE_CHOICE: MultipleChoiceEditor,
  FILL_BLANK: FillBlankEditor,
  SUBJECTIVE: SubjectiveEditor
}

const editorComponent = computed(() => editorMap[form.value.type] || SingleChoiceEditor)

onMounted(() => {
  if (!store.isLoggedIn) { router.push('/'); return }
  if (isEdit.value) loadQuestion()
})

function loadQuestion() {
  loading.value = true
  getQuestion(route.params.id).then(res => {
    const q = res.data
    form.value = {
      type: q.type,
      title: q.title,
      contentJson: q.contentJson,
      difficulty: q.difficulty,
      chapter: q.chapter,
      knowledgePoints: q.knowledgePoints || ''
    }
    if (q.attachments) {
      attachmentIds.value = q.attachments.map(a => a.id)
    }
  }).finally(() => { loading.value = false })
}

function onTypeChange() {
  form.value.contentJson = ''
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  const data = {
    teacherId: store.currentTeacher.id,
    type: form.value.type,
    title: form.value.title,
    contentJson: form.value.contentJson || '{}',
    difficulty: form.value.difficulty,
    chapter: form.value.chapter,
    knowledgePoints: form.value.knowledgePoints,
    attachmentIds: attachmentIds.value
  }

  submitting.value = true
  try {
    if (isEdit.value) {
      await updateQuestion(route.params.id, data)
      ElMessage.success('题目修改成功')
    } else {
      await createQuestion(data)
      ElMessage.success('题目创建成功')
    }
    router.push('/questions')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.question-form { max-width: 900px; margin: 0 auto; }
.page-title { margin-bottom: 16px; font-size: 20px; color: #303133; }
</style>
