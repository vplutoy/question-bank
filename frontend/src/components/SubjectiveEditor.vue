<template>
  <div class="editor">
    <el-form-item label="参考答案" required>
      <el-input
        v-model="referenceAnswer"
        type="textarea"
        :rows="4"
        placeholder="请输入参考答案"
        @input="emitUpdate"
      />
    </el-form-item>
    <el-form-item label="作答要求">
      <el-checkbox v-model="answerImageRequired" @change="emitUpdate">
        要求以作图形式回答（贴图作答）
      </el-checkbox>
    </el-form-item>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'

const props = defineProps({ modelValue: { type: String, default: '' } })
const emit = defineEmits(['update:modelValue'])

const referenceAnswer = ref('')
const answerImageRequired = ref(false)

function initFromJson(val) {
  if (!val) {
    referenceAnswer.value = ''
    answerImageRequired.value = false
    emitUpdate()
    return
  }
  try {
    const data = JSON.parse(val)
    referenceAnswer.value = data.referenceAnswer || ''
    answerImageRequired.value = data.answerImageRequired || false
  } catch {
    referenceAnswer.value = ''
    answerImageRequired.value = false
  }
  emitUpdate()
}

function emitUpdate() {
  emit('update:modelValue', JSON.stringify({
    referenceAnswer: referenceAnswer.value,
    answerImageRequired: answerImageRequired.value
  }))
}

watch(() => props.modelValue, initFromJson)
onMounted(() => initFromJson(props.modelValue))
</script>
