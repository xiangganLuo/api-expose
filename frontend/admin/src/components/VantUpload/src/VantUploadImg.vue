<template>
  <div class="vant-upload-img">
    <!-- 标题 -->
    <div v-if="uploadText" class="upload-title">
      {{ uploadText }}
      <span v-if="required" class="required-mark">*</span>
    </div>

    <!-- 上传区域 -->
    <div class="upload-container">
      <van-uploader
        v-model="uploaderFileList"
        :max-count="props.limit"
        :max-size="props.fileSize * 1024 * 1024"
        :accept="acceptTypes"
        :after-read="afterRead"
        :before-read="beforeRead"
        :before-delete="beforeDelete"
        :show-upload="canUpload"
        :deletable="!disabled"
        :preview-size="previewSize"
        :upload-icon="'photograph'"
        :upload-text="''"
        multiple
      >
        <!-- 自定义上传区域 -->
        <template #default>
          <div v-if="canUpload" class="custom-upload-area">
            <div class="upload-icon">
              <van-icon name="photograph" size="24" />
            </div>
            <div class="upload-text-area">
              <div class="main-text">上传图片</div>
              <div class="sub-text">轻触选择图片</div>
            </div>
          </div>
        </template>
      </van-uploader>

      <!-- 无法上传时的提示 - 已移除，达到最大数量时不显示提示 -->
    </div>

    <!-- 提示信息 -->
    <div v-if="placeholderText || isShowTip" class="upload-tip">
      <div v-if="placeholderText" class="tip-item">
        <van-icon name="info-o" size="14" />
        <span>{{ placeholderText }}</span>
      </div>
      <div v-if="isShowTip && !placeholderText" class="tip-content">
        <div class="tip-item">
          <van-icon name="info-o" size="14" />
          <span>大小不超过 {{ fileSize }}MB</span>
        </div>
        <div class="tip-item">
          <van-icon name="completed" size="14" />
          <span>支持 {{ fileType.join('、') }} 格式</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type {UploaderFileListItem} from 'vant'
import {showConfirmDialog, showToast} from 'vant'
import {useUpload} from '@/components/UploadFile/src/useUpload'

defineOptions({ name: 'VantUploadImg' })

const props = defineProps({
  modelValue: {
    type: [String, Array] as PropType<string | string[]>,
    default: () => []
  },
  fileType: {
    type: Array as PropType<string[]>,
    default: () => ['jpg', 'jpeg', 'png', 'gif', 'webp']
  },
  fileSize: {
    type: Number,
    default: 5
  },
  limit: {
    type: Number,
    default: 1
  },
  disabled: {
    type: Boolean,
    default: false
  },
  isShowTip: {
    type: Boolean,
    default: true
  },
  previewSize: {
    type: [String, Number],
    default: '80px'
  },
  directory: {
    type: String,
    default: undefined
  },
  uploadText: {
    type: String,
    default: ''
  },
  placeholderText: {
    type: String,
    default: ''
  },
  required: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue'])

// 上传相关
const { uploadUrl, httpRequest } = useUpload(props.directory)
const uploaderFileList = ref<UploaderFileListItem[]>([])

// 计算属性
const acceptTypes = computed(() => {
  return props.fileType.map(type => `image/${type === 'jpg' ? 'jpeg' : type}`).join(',')
})

const canUpload = computed(() => {
  return uploaderFileList.value.length < props.limit && !props.disabled
})

const uploadText = computed(() => {
  if (props.uploadText) {
    return props.uploadText
  }
  if (props.limit === 1) {
    return '上传图片'
  }
  return `上传图片 (${uploaderFileList.value.length}/${props.limit})`
})

// 上传前检查
const beforeRead = (file: File | File[]) => {
  const files = Array.isArray(file) ? file : [file]

  for (const currentFile of files) {
    // 检查文件类型
    const fileType = currentFile.type
    const isValidType = props.fileType.some(type => {
      const mimeType = type === 'jpg' ? 'image/jpeg' : `image/${type}`
      return fileType === mimeType
    })

    if (!isValidType) {
      showToast(`图片格式不正确，请上传${props.fileType.join('/')}格式!`)
      return false
    }

    // 检查文件大小
    const isLimit = currentFile.size < props.fileSize * 1024 * 1024
    if (!isLimit) {
      showToast(`上传图片大小不能超过${props.fileSize}MB!`)
      return false
    }

    // 检查数量限制
    if (uploaderFileList.value.length >= props.limit) {
      showToast(`上传图片数量不能超过${props.limit}张!`)
      return false
    }
  }

  return true
}

// 文件读取后上传
const afterRead = async (file: UploaderFileListItem | UploaderFileListItem[]) => {
  const files = Array.isArray(file) ? file : [file]

  for (const currentFile of files) {
    if (currentFile.file) {
      // 显示上传中状态
      currentFile.status = 'uploading'
      currentFile.message = '上传中...'

      try {
        // 执行上传
        const response = await httpRequest({
          file: currentFile.file,
          filename: currentFile.file.name
        } as any)

        // 上传成功
        currentFile.status = 'done'
        currentFile.message = ''

        // 更新文件URL
        const uploadedUrl = typeof response === 'string' ? response : (response as any)?.data
        if (uploadedUrl) {
          currentFile.url = uploadedUrl

          // 更新modelValue
          emitUpdateModelValue()
          showToast('上传成功')
        }
      } catch (error) {
        // 上传失败
        currentFile.status = 'failed'
        currentFile.message = '上传失败'
        showToast('上传失败，请重试')
      }
    }
  }
}

// 删除文件前确认
const beforeDelete = () => {
  return new Promise<boolean>((resolve) => {
    showConfirmDialog({
      title: '确认删除',
      message: '确定要删除这张图片吗？'
    }).then(() => {
      resolve(true)
      emitUpdateModelValue()
    }).catch(() => {
      resolve(false)
    })
  })
}

// 更新modelValue
const emitUpdateModelValue = () => {
  const urls = uploaderFileList.value
    .filter(item => item.url && item.status !== 'failed')
    .map(item => item.url!)

  if (props.limit === 1) {
    // 单图片模式返回字符串
    emit('update:modelValue', urls[0] || '')
  } else {
    // 多图片模式返回数组
    emit('update:modelValue', urls)
  }
}

// 监听modelValue变化
watch(
  () => props.modelValue,
  (val) => {
    if (!val) {
      uploaderFileList.value = []
      return
    }

    const newFileList: UploaderFileListItem[] = []

    if (typeof val === 'string') {
      if (val) {
        newFileList.push({
          url: val,
          status: 'done'
        })
      }
    } else if (Array.isArray(val)) {
      newFileList.push(...val.map(url => ({
        url: url,
        status: 'done' as const
      })))
    }

    uploaderFileList.value = newFileList
  },
  { immediate: true, deep: true }
)
</script>

<style lang="scss" scoped>
.vant-upload-img {
  width: 100%;
  position: relative;
}

.upload-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 16px;
  padding-left: 2px;
  position: relative;
}

.required-mark {
  color: #ee0a24;
  font-size: 14px;
  line-height: 1;
  margin-left: 4px;
}

.upload-container {
  margin-bottom: 16px;

  :deep(.van-uploader) {
    .van-uploader__wrapper {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
    }

    .van-uploader__upload {
      display: none; // 隐藏默认上传按钮
    }

    .van-uploader__preview {
      width: 80px;
      height: 80px;
      border-radius: 8px;
      overflow: hidden;
      margin: 0;
      border: 1px solid #f0f0f0;

      .van-uploader__preview-image {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .van-uploader__preview-delete {
        background: rgba(0, 0, 0, 0.6);

        .van-icon {
          color: white;
          font-size: 16px;
        }
      }
    }
  }

  // 自定义上传区域 - 极简版本
  .custom-upload-area {
    background: #fafafa;
    border: 1px dashed #d9d9d9;
    border-radius: 8px;
    padding: 16px;
    text-align: center;
    transition: border-color 0.2s ease;
    cursor: pointer;
    min-height: 80px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;

    &:hover {
      border-color: #1890ff;
    }

    .upload-icon {
      margin-bottom: 8px;

      .van-icon {
        color: #bfbfbf;
        font-size: 24px;
      }
    }

    .upload-text-area {
      .main-text {
        font-size: 14px;
        color: #666;
        margin-bottom: 4px;
      }

      .sub-text {
        font-size: 12px;
        color: #999;
      }
    }
  }
}

// 注释：上传完成状态样式已移除，不再显示任何提示

// 提示信息 - 超简版本
.upload-tip {
  background: #f5f5f5;
  border-radius: 4px;
  padding: 8px 12px;
  font-size: 12px;
  color: #999;

  .tip-item {
    display: flex;
    align-items: center;
    gap: 6px;
    margin-bottom: 4px;

    &:last-child {
      margin-bottom: 0;
    }

    .van-icon {
      color: #ccc;
      flex-shrink: 0;
      font-size: 12px;
    }
  }

  .tip-content {
    .tip-item {
      margin-bottom: 2px;

      &:last-child {
        margin-bottom: 0;
      }
    }
  }
}

// 响应式优化 - 简化版本
@media (max-width: 375px) {
  .upload-title {
    font-size: 15px;
    margin-bottom: 14px;
  }

  .upload-container {
    .custom-upload-area {
      padding: 14px;
      min-height: 70px;

      .upload-icon {
        margin-bottom: 6px;

        .van-icon {
          font-size: 20px;
        }
      }

      .upload-text-area {
        .main-text {
          font-size: 13px;
        }

        .sub-text {
          font-size: 11px;
        }
      }
    }

    :deep(.van-uploader) {
      .van-uploader__preview {
        width: 70px;
        height: 70px;
      }
    }
  }
}
</style>
