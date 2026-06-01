<template>
  <div class="editor">
    <el-form-item label="选项设置" required>
      <div v-for="(opt, idx) in options" :key="idx" class="option-row">
        <el-input v-model="opt.label" placeholder="选项标签" style="width:80px" maxlength="2" @input="emitUpdate" />
        <el-input v-model="opt.text" placeholder="选项内容" style="flex:1" @input="emitUpdate" />
        <el-checkbox v-model="opt.isCorrect" @change="emitUpdate">正确答案</el-checkbox>
        <el-button type="danger" :icon="Delete" circle size="small" @click="removeOption(idx)" :disabled="options.length <= 3" />
      </div>
      <el-button type="primary" link @click="addOption">+ 添加选项</el-button>
    </el-form-item>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'

const props = defineProps({ modelValue: { type: String, default: '' } })
const emit = defineEmits(['update:modelValue'])

const options = ref([])

function initFromJson(val) {
  if (!val) {
    options.value = [
      { label: 'A', text: '', isCorrect: true },
      { label: 'B', text: '', isCorrect: true },
      { label: 'C', text: '', isCorrect: false }
    ]
    emitUpdate()
    return
  }
  try {
    const data = JSON.parse(val)
    options.value = data.options || []
  } catch {
    options.value = [
      { label: 'A', text: '', isCorrect: true },
      { label: 'B', text: '', isCorrect: true },
      { label: 'C', text: '', isCorrect: false }
    ]
  }
  emitUpdate()
}

function emitUpdate() {
  emit('update:modelValue', JSON.stringify({ options: options.value }))
}

function addOption() {
  const label = String.fromCharCode(65 + options.value.length)
  options.value.push({ label, text: '', isCorrect: false })
  emitUpdate()
}

function removeOption(idx) {
  options.value.splice(idx, 1)
  emitUpdate()
}

watch(() => props.modelValue, initFromJson)
onMounted(() => initFromJson(props.modelValue))
</script>

<style scoped>
.option-row { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }
</style>
