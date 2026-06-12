<template>
  <AppLayout>
    <div class="analysis" v-loading="loading">
      <!-- 学生信息头部 -->
      <el-card style="margin-bottom:16px">
        <div class="student-header">
          <el-button link type="primary" @click="router.back()">
            <el-icon><ArrowLeft /></el-icon> 返回
          </el-button>
          <div class="header-info">
            <h2 style="margin:0">{{ studentName }} 的学情分析</h2>
            <span style="color:#909399">学号：{{ studentNo }} | 班级：{{ className }}</span>
          </div>
        </div>
      </el-card>

      <el-tabs v-model="activeTab" type="border-card">
        <!-- Tab1: 成绩趋势 -->
        <el-tab-pane label="成绩趋势" name="trend">
          <div ref="trendChartRef" style="width:100%;height:400px"></div>
          <el-empty v-if="trendData.length === 0" description="暂无考试成绩记录" />
        </el-tab-pane>

        <!-- Tab2: 错题知识点分析 -->
        <el-tab-pane label="错误知识点分析" name="error">
          <div ref="errorChartRef" style="width:100%;height:400px"></div>
          <el-empty v-if="errorData.length === 0" description="暂无错题记录" />
          <el-table v-if="errorData.length > 0" :data="errorData" stripe style="margin-top:16px">
            <el-table-column prop="knowledgePoint" label="知识点" min-width="200" />
            <el-table-column prop="errorCount" label="出错次数" width="110" />
            <el-table-column prop="totalCount" label="涉及次数" width="110" />
            <el-table-column label="错误率" width="110">
              <template #default="{ row }">
                <el-tag :type="getErrorRateType(row.errorRate)">
                  {{ formatPercent(row.errorRate) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useTeacherStore } from '../../stores/teacher'
import AppLayout from '../../components/AppLayout.vue'
import { getScoreTrend, getErrorAnalysis, getStudents } from '../../api/student'
import * as echarts from 'echarts'

const route = useRoute()
const router = useRouter()
const store = useTeacherStore()

const studentNo = ref(route.params.studentNo)
const studentName = ref('')
const className = ref('')
const loading = ref(false)
const activeTab = ref('trend')

const trendData = ref([])
const errorData = ref([])
const trendChartRef = ref(null)
const errorChartRef = ref(null)

let trendChart = null
let errorChart = null

onMounted(() => {
  if (!store.isLoggedIn) { router.push('/'); return }
  loadStudentInfo()
  loadTrend()
  loadError()
})

function loadStudentInfo() {
  // 从学生列表 API 中查找该学生的基本信息
  getStudents({ size: 1, keyword: studentNo.value }).then(res => {
    const records = res.data.records
    if (records && records.length > 0) {
      const s = records[0]
      studentName.value = s.name
      className.value = s.className || ''
    } else {
      studentName.value = studentNo.value
    }
  })
}

function loadTrend() {
  loading.value = true
  getScoreTrend(studentNo.value).then(res => {
    trendData.value = res.data || []
    nextTick(() => { renderTrendChart() })
  }).finally(() => { loading.value = false })
}

function loadError() {
  getErrorAnalysis(studentNo.value).then(res => {
    errorData.value = res.data || []
    nextTick(() => { renderErrorChart() })
  })
}

function renderTrendChart() {
  if (!trendChartRef.value || trendData.value.length === 0) return
  if (trendChart) trendChart.dispose()

  trendChart = echarts.init(trendChartRef.value)
  trendChart.setOption({
    title: { text: '历次考试成绩趋势', left: 'center' },
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: trendData.value.map(d => d.examName || '考试' + d.examId)
    },
    yAxis: {
      type: 'value',
      name: '分数'
    },
    series: [{
      name: '得分',
      type: 'line',
      data: trendData.value.map(d => d.totalScore),
      smooth: true,
      label: { show: true },
      markLine: {
        silent: true,
        data: trendData.value[0]?.paperTotalScore
          ? [{ yAxis: trendData.value[0].paperTotalScore, label: { formatter: '满分 {c}' }, lineStyle: { type: 'dashed' } }]
          : []
      }
    }]
  })
}

function renderErrorChart() {
  if (!errorChartRef.value || errorData.value.length === 0) return
  if (errorChart) errorChart.dispose()

  errorChart = echarts.init(errorChartRef.value)

  // 只取前15个
  const top = errorData.value.slice(0, 15)

  errorChart.setOption({
    title: { text: '知识点错误分布（Top 15）', left: 'center' },
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'value',
      name: '出错次数'
    },
    yAxis: {
      type: 'category',
      data: top.map(d => d.knowledgePoint).reverse(),
      axisLabel: { interval: 0 }
    },
    series: [{
      name: '出错次数',
      type: 'bar',
      data: top.map(d => d.errorCount).reverse(),
      itemStyle: { color: '#e74c3c' }
    }]
  })
}

function getErrorRateType(rate) {
  if (rate >= 0.5) return 'danger'
  if (rate >= 0.2) return 'warning'
  return 'success'
}

function formatPercent(rate) {
  if (rate == null) return '0%'
  return (rate * 100).toFixed(1) + '%'
}

// 切换 tab 时重绘图表
watch(activeTab, (tab) => {
  nextTick(() => {
    if (tab === 'trend') renderTrendChart()
    else renderErrorChart()
  })
})
</script>

<style scoped>
.analysis { max-width: 1200px; margin: 0 auto; }
.student-header {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}
.header-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
</style>
