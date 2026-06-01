<template>
  <div class="image-uploader">
    <el-upload
      :action="uploadUrl"
      :headers="{}"
      list-type="picture-card"
      :on-success="handleSuccess"
      :on-error="handleError"
      :on-remove="handleRemove"
      :file-list="fileList"
      :before-upload="beforeUpload"
    >
      <el-icon><Plus /></el-icon>
    </el-upload>
    <p class="upload-tip">支持 jpg/png/gif/webp，单张不超过 5MB</p>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { uploadFile, deleteAttachment } from '../api/file'

const props = defineProps({
  modelValue: { type: Array, default: () => [] },
  uploadUrl: { type: String, default: '/api/upload' }
})

const emit = defineEmits(['update:modelValue'])

const uploadedIds = ref([...props.modelValue])
const fileList = ref([])

function beforeUpload(file) {
  const isValid = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'].includes(file.type)
  if (!isValid) {
    ElMessage.error('只支持 jpg/png/gif/webp 格式的图片')
    return false
  }
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB')
    return false
  }
  return true
}

async function handleSuccess(response) {
  uploadedIds.value.push(response.id)
  emit('update:modelValue', uploadedIds.value)
}

function handleError() {
  ElMessage.error('上传失败，请重试')
}

async function handleRemove(file) {
  if (file.response?.id) {
    const idx = uploadedIds.value.indexOf(file.response.id)
    if (idx > -1) {
      uploadedIds.value.splice(idx, 1)
      emit('update:modelValue', uploadedIds.value)
    }
    try { await deleteAttachment(file.response.id) } catch (e) { /* ignore */ }
  } else if (file.id) {
    const idx = uploadedIds.value.indexOf(file.id)
    if (idx > -1) {
      uploadedIds.value.splice(idx, 1)
      emit('update:modelValue', uploadedIds.value)
    }
  }
}
</script>

<style scoped>
.image-uploader { margin: 8px 0; }
.upload-tip { color: #999; font-size: 12px; margin-top: 4px; }
</style>
