<template>
  <AppLayout>
    <div class="kp-page">
      <div class="header">
        <h2>知识点管理</h2>
        <el-button type="primary" @click="openAdd">新增知识点</el-button>
      </div>

      <div class="toolbar">
        <el-select v-model="filterChapter" placeholder="按章节筛选" clearable style="width:240px" @change="load">
          <el-option v-for="ch in chapters" :key="ch.id" :label="ch.name" :value="ch.id" />
        </el-select>
      </div>

      <el-card shadow="never">
        <el-table :data="knowledgePoints" v-loading="loading" stripe>
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column label="所属章节" width="200" show-overflow-tooltip>
            <template #default="{ row }">{{ chapterName(row.chapterId) }}</template>
          </el-table-column>
          <el-table-column prop="name" label="知识点名称" min-width="180" />
          <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
          <el-table-column label="操作" width="140" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="openEdit(row)">编辑</el-button>
              <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)">
                <template #reference>
                  <el-button link type="danger" size="small">删除</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑知识点' : '新增知识点'" width="500px">
        <el-form :model="dialogForm" label-width="90px">
          <el-form-item label="所属章节" required>
            <el-select v-model="dialogForm.chapterId" placeholder="选择题所属章节" style="width:100%">
              <el-option v-for="ch in chapters" :key="ch.id" :label="ch.name" :value="ch.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="名称" required>
            <el-input v-model="dialogForm.name" placeholder="请输入知识点名称" />
          </el-form-item>
          <el-form-item label="描述">
            <el-input v-model="dialogForm.description" type="textarea" :rows="2" placeholder="知识点的简要描述" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
        </template>
      </el-dialog>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useTeacherStore } from '../../stores/teacher'
import AppLayout from '../../components/AppLayout.vue'
import { getKnowledgePoints, createKnowledgePoint, updateKnowledgePoint, deleteKnowledgePoint } from '../../api/knowledgePoint'
import { getChapters } from '../../api/chapter'

const router = useRouter()
const store = useTeacherStore()
const chapters = ref([])
const knowledgePoints = ref([])
const filterChapter = ref(null)
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const saving = ref(false)
const dialogForm = ref({ chapterId: null, name: '', description: '' })
let editingId = null

function chapterName(id) {
  const ch = chapters.value.find(c => c.id === id)
  return ch ? ch.name : '未分配'
}

function load() {
  loading.value = true
  getKnowledgePoints(filterChapter.value).then(res => {
    knowledgePoints.value = res.data
  }).finally(() => { loading.value = false })
}

onMounted(async () => {
  if (!store.isLoggedIn) { router.push('/'); return }
  const chRes = await getChapters()
  chapters.value = chRes.data
  load()
})

function openAdd() {
  isEdit.value = false; editingId = null
  dialogForm.value = { chapterId: filterChapter.value || (chapters.value[0]?.id || null), name: '', description: '' }
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true; editingId = row.id
  dialogForm.value = { chapterId: row.chapterId, name: row.name, description: row.description || '' }
  dialogVisible.value = true
}

async function handleSave() {
  if (!dialogForm.value.name.trim() || !dialogForm.value.chapterId) {
    ElMessage.warning('请填写名称并选择章节'); return
  }
  saving.value = true
  try {
    if (isEdit.value) await updateKnowledgePoint(editingId, dialogForm.value)
    else await createKnowledgePoint(dialogForm.value)
    ElMessage.success(isEdit.value ? '修改成功' : '添加成功')
    dialogVisible.value = false; load()
  } finally { saving.value = false }
}

async function handleDelete(id) {
  await deleteKnowledgePoint(id)
  ElMessage.success('已删除'); load()
}
</script>

<style scoped>
.kp-page { max-width: 1100px; margin: 0 auto; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.header h2 { font-size: 20px; color: #303133; }
.toolbar { margin-bottom: 12px; }
</style>
