<template>
  <AppLayout>
    <div class="student-manage">
      <div class="toolbar">
        <h2 style="margin:0">学生管理</h2>
        <div class="spacer" />
        <el-button type="primary" @click="openDialog()">新建学生</el-button>
      </div>

      <!-- 筛选栏 -->
      <el-card style="margin-bottom:16px">
        <el-row :gutter="16" align="middle">
          <el-col :span="6">
            <el-select v-model="filterClassId" placeholder="按班级筛选" clearable @change="search">
              <el-option
                v-for="c in classList"
                :key="c.id"
                :label="c.className"
                :value="c.id"
              />
            </el-select>
          </el-col>
          <el-col :span="6">
            <el-input v-model="filterKeyword" placeholder="搜索姓名/学号" clearable @keyup.enter="search" />
          </el-col>
          <el-col :span="4">
            <el-button type="primary" @click="search">搜索</el-button>
          </el-col>
        </el-row>
      </el-card>

      <el-table :data="students" stripe v-loading="loading" style="width:100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="studentNo" label="学号" width="140" />
        <el-table-column prop="className" label="班级" min-width="140" />
        <el-table-column prop="department" label="院系" min-width="120" />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除此学生？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button link type="danger" size="small">删除</el-button>
              </template>
            </el-popconfirm>
            <el-button link type="success" size="small" @click="goAnalysis(row)">分析</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div style="margin-top:16px; text-align:right">
        <el-pagination
          v-model:current-page="page"
          :page-size="size"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="search"
        />
      </div>

      <!-- 新建/编辑弹窗 -->
      <el-dialog v-model="dialogVisible" :title="editingId ? '编辑学生' : '新建学生'" width="480px">
        <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
          <el-form-item label="姓名" prop="name">
            <el-input v-model="form.name" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="学号" prop="studentNo">
            <el-input v-model="form.studentNo" placeholder="请输入学号" />
          </el-form-item>
          <el-form-item label="班级" prop="classId">
            <el-select v-model="form.classId" placeholder="请选择班级" clearable style="width:100%">
              <el-option
                v-for="c in classList"
                :key="c.id"
                :label="c.className + ' (' + (c.department || '') + ')'"
                :value="c.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="院系" prop="department">
            <el-input v-model="form.department" placeholder="请输入院系" />
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
import { getStudents, createStudent, updateStudent, deleteStudent, getStudentClasses } from '../../api/student'

const router = useRouter()
const store = useTeacherStore()
const students = ref([])
const classList = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const editingId = ref(null)
const formRef = ref()

const page = ref(1)
const size = ref(10)
const total = ref(0)
const filterClassId = ref(null)
const filterKeyword = ref('')

const form = ref({ name: '', studentNo: '', classId: null, department: '' })
const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }]
}

onMounted(() => {
  if (!store.isLoggedIn) { router.push('/'); return }
  getStudentClasses().then(res => { classList.value = res.data })
  search()
})

function search() {
  loading.value = true
  getStudents({
    page: page.value,
    size: size.value,
    classId: filterClassId.value || undefined,
    keyword: filterKeyword.value || undefined
  }).then(res => {
    students.value = res.data.records
    total.value = res.data.total
  }).finally(() => { loading.value = false })
}

function openDialog(row = null) {
  editingId.value = row?.id ?? null
  if (row) {
    form.value = { name: row.name, studentNo: row.studentNo, classId: row.classId, department: row.department || '' }
  } else {
    form.value = { name: '', studentNo: '', classId: null, department: '' }
  }
  dialogVisible.value = true
}

function handleSubmit() {
  formRef.value.validate(valid => {
    if (!valid) return
    submitting.value = true
    const payload = { ...form.value }
    const req = editingId.value
      ? updateStudent(editingId.value, payload)
      : createStudent(payload)
    req.then(() => {
      ElMessage.success(editingId.value ? '更新成功' : '创建成功')
      dialogVisible.value = false
      search()
    }).finally(() => { submitting.value = false })
  })
}

function handleDelete(id) {
  deleteStudent(id).then(() => {
    ElMessage.success('删除成功')
    search()
  })
}

function goAnalysis(row) {
  router.push(`/students/analysis/${row.studentNo}`)
}
</script>

<style scoped>
.student-manage { max-width: 1200px; margin: 0 auto; }
.toolbar { display: flex; align-items: center; margin-bottom: 16px; }
.spacer { flex: 1; }
</style>
