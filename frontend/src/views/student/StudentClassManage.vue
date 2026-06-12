<template>
  <AppLayout>
    <div class="class-manage">
      <div class="toolbar">
        <h2 style="margin:0">班级管理</h2>
        <div class="spacer" />
        <el-button type="primary" @click="openDialog()">新建班级</el-button>
      </div>

      <el-table :data="classes" stripe v-loading="loading" style="width:100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="className" label="班级名称" min-width="160" />
        <el-table-column prop="department" label="院系" min-width="120" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="studentCount" label="学生人数" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除此班级？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button link type="danger" size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <el-dialog v-model="dialogVisible" :title="editingId ? '编辑班级' : '新建班级'" width="480px">
        <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
          <el-form-item label="班级名称" prop="className">
            <el-input v-model="form.className" placeholder="如：软件工程2001班" />
          </el-form-item>
          <el-form-item label="院系" prop="department">
            <el-input v-model="form.department" placeholder="如：计算机学院" />
          </el-form-item>
          <el-form-item label="描述" prop="description">
            <el-input v-model="form.description" type="textarea" :rows="2" placeholder="班级描述（选填）" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">保存</el-button>
        </template>
      </el-dialog>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useTeacherStore } from '../../stores/teacher'
import AppLayout from '../../components/AppLayout.vue'
import { getStudentClasses, createStudentClass, updateStudentClass, deleteStudentClass } from '../../api/student'

const router = useRouter()
const store = useTeacherStore()
const classes = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const editingId = ref(null)
const formRef = ref()

const form = ref({ className: '', department: '', description: '' })
const rules = {
  className: [{ required: true, message: '请输入班级名称', trigger: 'blur' }]
}

onMounted(() => {
  if (!store.isLoggedIn) { router.push('/'); return }
  loadClasses()
})

function loadClasses() {
  loading.value = true
  getStudentClasses().then(res => { classes.value = res.data }).finally(() => { loading.value = false })
}

function openDialog(row = null) {
  editingId.value = row?.id ?? null
  if (row) {
    form.value = { className: row.className, department: row.department || '', description: row.description || '' }
  } else {
    form.value = { className: '', department: '', description: '' }
  }
  dialogVisible.value = true
}

function handleSubmit() {
  formRef.value.validate(valid => {
    if (!valid) return
    submitting.value = true
    const payload = { ...form.value }
    const req = editingId.value
      ? updateStudentClass(editingId.value, payload)
      : createStudentClass(payload)
    req.then(() => {
      ElMessage.success(editingId.value ? '更新成功' : '创建成功')
      dialogVisible.value = false
      loadClasses()
    }).finally(() => { submitting.value = false })
  })
}

function handleDelete(id) {
  deleteStudentClass(id).then(() => {
    ElMessage.success('删除成功')
    loadClasses()
  })
}
</script>

<style scoped>
.class-manage { max-width: 1100px; margin: 0 auto; }
.toolbar { display: flex; align-items: center; margin-bottom: 16px; }
.spacer { flex: 1; }
</style>
