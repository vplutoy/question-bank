<template>
  <div class="editor">
    <el-form-item label="填空答案" required>
      <div v-for="(blank, idx) in blanks" :key="idx" class="blank-row">
        <span>第 {{ idx + 1 }} 空：</span>
        <el-input v-model="blank.answer" placeholder="请输入答案" style="flex:1" @input="emitUpdate" />
        <el-input-number v-model="blank.score" :min="1" :max="100" style="width:120px" controls-position="right" @change="emitUpdate" />
        <span>分</span>
        <el-button type="danger" :icon="Delete" circle size="small" @click="removeBlank(idx)" :disabled="blanks.length <= 1" />
      </div>
      <el-button type="primary" link @click="addBlank">+ 添加填空</el-button>
    </el-form-item>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'

const props = defineProps({ modelValue: { type: String, default: '' } })
const emit = defineEmits(['update:modelValue'])

const blanks = ref([])

function initFromJson(val) {
  if (!val) {
    blanks.value = [{ order: 1, answer: '', score: 5 }]
    emitUpdate()
    return
  }
  try {
    const data = JSON.parse(val)
    blanks.value = data.blanks || []
  } catch {
    blanks.value = [{ order: 1, answer: '', score: 5 }]
  }
  emitUpdate()
}

function emitUpdate() {
  blanks.value.forEach((b, i) => { b.order = i + 1 })
  emit('update:modelValue', JSON.stringify({ blanks: blanks.value }))
}

function addBlank() {
  blanks.value.push({ order: blanks.value.length + 1, answer: '', score: 5 })
  emitUpdate()
}

function removeBlank(idx) {
  blanks.value.splice(idx, 1)
  emitUpdate()
}

watch(() => props.modelValue, initFromJson)
onMounted(() => initFromJson(props.modelValue))
</script>

<style scoped>
.blank-row { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }
</style>
