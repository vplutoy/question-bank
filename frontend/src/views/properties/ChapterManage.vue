<template>
  <AppLayout>
    <div class="chapter-page">
      <div class="header">
        <h2>章节管理</h2>
        <el-button type="primary" @click="openAdd()">新增章节</el-button>
      </div>

      <el-card shadow="never">
        <el-tree :data="treeData" node-key="id" default-expand-all :props="{ children: 'children', label: 'name' }">
          <template #default="{ data }">
            <span class="tree-node">
              <span>{{ data.name }}</span>
              <span class="actions">
                <el-button link type="primary" size="small" @click.stop="openAdd(data)">添加子章节</el-button>
                <el-button link type="primary" size="small" @click.stop="openEdit(data)">编辑</el-button>
                <el-popconfirm title="删除此章节将同时删除子章节，确定？" @confirm="handleDelete(data.id)">
                  <template #reference>
                    <el-button link type="danger" size="small" @click.stop>删除</el-button>
                  </template>
                </el-popconfirm>
              </span>
            </span>
          </template>
        </el-tree>
      </el-card>

      <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑章节' : '新增章节'" width="450px">
        <el-form :model="dialogForm" label-width="80px">
          <el-form-item label="父章节">
            <el-tag v-if="parentName" type="info">{{ parentName }}</el-tag>
            <span v-else>无（根章节）</span>
          </el-form-item>
          <el-form-item label="名称" required>
            <el-input v-model="dialogForm.name" placeholder="请输入章节名称" />
          </el-form-item>
          <el-form-item label="排序">
            <el-input-number v-model="dialogForm.sortOrder" :min="0" :max="999" />
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
import { getChapters, createChapter, updateChapter, deleteChapter } from '../../api/chapter'

const router = useRouter()
const store = useTeacherStore()
const chapters = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const saving = ref(false)
const parentName = ref('')
const dialogForm = ref({ parentId: 0, name: '', sortOrder: 0 })
let editingId = null

const treeData = ref([])

function buildTree(list) {
  const map = {}, roots = []
  list.forEach(item => { map[item.id] = { ...item, children: [] } })
  list.forEach(item => {
    if (item.parentId && map[item.parentId]) map[item.parentId].children.push(map[item.id])
    else roots.push(map[item.id])
  })
  return roots
}

function load() {
  getChapters().then(res => { chapters.value = res.data; treeData.value = buildTree(res.data) })
}

function findName(id) {
  const c = chapters.value.find(c => c.id === id)
  return c ? c.name : ''
}

function openAdd(parent) {
  isEdit.value = false; editingId = null
  dialogForm.value = { parentId: parent ? parent.id : 0, name: '', sortOrder: 0 }
  parentName.value = parent ? parent.name : ''
  dialogVisible.value = true
}

function openEdit(data) {
  isEdit.value = true; editingId = data.id
  dialogForm.value = { parentId: data.parentId || 0, name: data.name, sortOrder: data.sortOrder || 0 }
  parentName.value = data.parentId ? findName(data.parentId) : ''
  dialogVisible.value = true
}

async function handleSave() {
  if (!dialogForm.value.name.trim()) { ElMessage.warning('请输入名称'); return }
  saving.value = true
  try {
    if (isEdit.value) await updateChapter(editingId, dialogForm.value)
    else await createChapter(dialogForm.value)
    ElMessage.success(isEdit.value ? '修改成功' : '添加成功')
    dialogVisible.value = false; load()
  } finally { saving.value = false }
}

async function handleDelete(id) {
  await deleteChapter(id)
  ElMessage.success('已删除'); load()
}

onMounted(() => { if (!store.isLoggedIn) router.push('/'); else load() })
</script>

<style scoped>
.chapter-page { max-width: 800px; margin: 0 auto; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.header h2 { font-size: 20px; color: #303133; }
.tree-node { display: flex; align-items: center; justify-content: space-between; width: 100%; padding-right: 16px; }
.actions { display: flex; gap: 4px; }
</style>
