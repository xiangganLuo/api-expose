<template>
  <div class="vant-upload-file">
    <!-- 标题 -->
    <div v-if="uploadTitle" class="upload-title">
      {{ uploadTitle }}
      <span v-if="required" class="required-mark">*</span>
    </div>

    <!-- 文件列表显示 -->
    <div v-if="fileList.length > 0" class="file-list">
      <div
        v-for="(file, index) in fileList"
        :key="index"
        class="file-item"
        @click="downloadFile(file)"
      >
        <div class="file-info">
          <div class="file-icon">
            <van-icon name="description" size="20" />
          </div>
          <div class="file-details">
            <div class="file-name">{{ file.name }}</div>
            <div class="file-size">{{ formatFileSize(file.size) }}</div>
          </div>
        </div>
        <div class="file-actions">
          <van-icon name="arrow" class="download-icon" />
          <van-button
            v-if="!disabled"
            size="small"
            type="danger"
            plain
            round
            @click.stop="removeFile(index)"
            class="delete-btn"
          >
            删除
          </van-button>
        </div>
      </div>
    </div>

    <!-- 上传区域 - 现代化卡片设计 -->
    <div v-if="!disabled && canUpload" class="upload-area">
      <van-uploader
        v-model="uploaderFileList"
        :max-count="props.limit"
        :max-size="props.fileSize * 1024 * 1024"
        :accept="acceptTypes"
        :after-read="afterRead"
        :before-read="beforeRead"
        :show-upload="canUpload"
        :deletable="false"
        multiple
      >
        <div class="custom-upload-area">
          <div class="upload-icon">
            <van-icon name="plus" size="24" />
          </div>
          <div class="upload-text-area">
            <div class="main-text">{{ uploadText }}</div>
            <div class="sub-text">轻触选择文件</div>
          </div>
        </div>

        <template #preview-cover="{ file }">
          <div class="file-preview-cover">
            <van-icon name="description" size="24" />
            <div class="file-name">{{ getFileName(file) }}</div>
          </div>
        </template>
      </van-uploader>
    </div>

    <!-- 无法上传时的提示 - 已移除，达到最大数量时不显示提示 -->

    <!-- 提示信息 -->
    <div v-if="isShowTip" class="upload-tip">
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
</template>

<script setup lang="ts">
import type {UploaderFileListItem} from 'vant'
import {showConfirmDialog, showToast} from 'vant'
import {useUpload} from '@/components/UploadFile/src/useUpload'

defineOptions({ name: 'VantUploadFile' })

interface FileItem {
  name: string
  url: string
  size?: number
}

const props = defineProps({
  modelValue: {
    type: [String, Array] as PropType<string | string[]>,
    default: () => []
  },
  fileType: {
    type: Array as PropType<string[]>,
    default: () => ['doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx', 'txt', 'pdf']
  },
  fileSize: {
    type: Number,
    default: 5
  },
  limit: {
    type: Number,
    default: 5
  },
  disabled: {
    type: Boolean,
    default: false
  },
  isShowTip: {
    type: Boolean,
    default: true
  },
  directory: {
    type: String,
    default: undefined
  },
  uploadTitle: {
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
const fileList = ref<FileItem[]>([])

// 计算属性
const acceptTypes = computed(() => {
  return props.fileType.map(type => {
    const typeMap: Record<string, string> = {
      'doc': '.doc',
      'docx': '.docx',
      'xls': '.xls',
      'xlsx': '.xlsx',
      'ppt': '.ppt',
      'pptx': '.pptx',
      'txt': '.txt',
      'pdf': '.pdf',
      'jpg': '.jpg',
      'jpeg': '.jpeg',
      'png': '.png',
      'gif': '.gif'
    }
    return typeMap[type] || `.${type}`
  }).join(',')
})

const canUpload = computed(() => {
  return fileList.value.length < props.limit
})

const uploadText = computed(() => {
  return `上传文件 (${fileList.value.length}/${props.limit})`
})

// 文件大小格式化
const formatFileSize = (size?: number) => {
  if (!size) return ''
  if (size < 1024) return `${size}B`
  if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)}KB`
  return `${(size / 1024 / 1024).toFixed(1)}MB`
}

// 上传前检查
const beforeRead = (file: File | File[]) => {
  const files = Array.isArray(file) ? file : [file]

  for (const currentFile of files) {
    // 检查文件类型
    const fileName = currentFile.name.toLowerCase()
    const fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1)
    if (!props.fileType.includes(fileExtension)) {
      showToast(`文件格式不正确，请上传${props.fileType.join('/')}格式!`)
      return false
    }

    // 检查文件大小
    const isLimit = currentFile.size < props.fileSize * 1024 * 1024
    if (!isLimit) {
      showToast(`上传文件大小不能超过${props.fileSize}MB!`)
      return false
    }

    // 检查数量限制
    if (fileList.value.length >= props.limit) {
      showToast(`上传文件数量不能超过${props.limit}个!`)
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

        // 添加到文件列表
        const uploadedUrl = typeof response === 'string' ? response : (response as any)?.data
        if (uploadedUrl) {
          fileList.value.push({
            name: currentFile.file.name,
            url: uploadedUrl,
            size: currentFile.file.size
          })

          // 清空 uploaderFileList，使用自定义文件列表展示
          uploaderFileList.value = []

          // 更新modelValue
          emitUpdateModelValue()
          showToast('上传成功')
        }
      } catch (error) {
        // 上传失败
        currentFile.status = 'failed'
        currentFile.message = '上传失败'

        // 上传失败后清空 uploaderFileList，让用户可以重新上传
        uploaderFileList.value = []

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
      message: '确定要删除这个文件吗？'
    }).then(() => {
      resolve(true)
    }).catch(() => {
      resolve(false)
    })
  })
}

// 移除文件
const removeFile = (index: number) => {
  const file = fileList.value[index]
  const fileName = file?.name || '此文件'

  showConfirmDialog({
    title: '确认删除',
    message: `确定要删除"${fileName}"吗？`
  }).then(() => {
    // 同时清理两个文件列表
    fileList.value.splice(index, 1)

    // 清空 uploaderFileList，确保 van-uploader 组件状态正确
    uploaderFileList.value = []

    emitUpdateModelValue()
  }).catch(() => {
    // 用户取消
  })
}

// 下载文件
const downloadFile = (file: FileItem) => {
  window.open(file.url, '_blank')
}

// 获取文件名（用于预览）
const getFileName = (file: UploaderFileListItem) => {
  // 优先使用原始文件名
  if (file.file?.name) {
    return file.file.name
  }

  // 其次从 URL 中提取文件名
  if (file.url) {
    const urlFileName = file.url.split('/').pop()
    if (urlFileName && urlFileName !== '') {
      return decodeURIComponent(urlFileName)
    }
  }

  // 最后返回默认名称
  return '文件'
}

// 更新modelValue
const emitUpdateModelValue = () => {
  const urls = fileList.value.map(file => file.url)

  if (props.limit === 1) {
    // 单文件模式返回字符串
    emit('update:modelValue', urls[0] || '')
  } else {
    // 多文件模式返回数组
    emit('update:modelValue', urls)
  }
}

// 监听modelValue变化
watch(
  () => props.modelValue,
  (val) => {
    if (!val) {
      fileList.value = []
      // 同时清空 uploaderFileList
      uploaderFileList.value = []
      return
    }

    fileList.value = []
    // 清空 uploaderFileList，使用自定义文件列表展示
    uploaderFileList.value = []

    if (typeof val === 'string') {
      if (val) {
        // 从URL中提取文件名，并解码
        let fileName = val.substring(val.lastIndexOf('/') + 1)
        if (fileName) {
          fileName = decodeURIComponent(fileName)
        } else {
          fileName = '文件'
        }

        fileList.value.push({
          name: fileName,
          url: val,
          size: undefined
        })
      }
    } else if (Array.isArray(val)) {
      fileList.value = val.map(url => {
        // 从URL中提取文件名，并解码
        let fileName = url.substring(url.lastIndexOf('/') + 1)
        if (fileName) {
          fileName = decodeURIComponent(fileName)
        } else {
          fileName = '文件'
        }

        return {
          name: fileName,
          url,
          size: undefined
        }
      })
    }
  },
  { immediate: true, deep: true }
)
</script>

<style lang="scss" scoped>
.vant-upload-file {
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

// 文件列表 - 极简版本
.file-list {
  margin-bottom: 16px;

  .file-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    background: #fafafa;
    border: 1px solid #e8e8e8;
    border-radius: 8px;
    padding: 12px;
    margin-bottom: 8px;
    transition: border-color 0.2s ease;
    cursor: pointer;

    &:hover {
      border-color: #1890ff;
    }

    .file-info {
      display: flex;
      align-items: center;
      flex: 1;
      min-width: 0;

      .file-icon {
        margin-right: 12px;
        flex-shrink: 0;

        .van-icon {
          color: #8c8c8c;
          font-size: 20px;
        }
      }

      .file-details {
        flex: 1;
        min-width: 0;

        .file-name {
          font-size: 14px;
          color: #262626;
          margin-bottom: 4px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .file-size {
          font-size: 12px;
          color: #8c8c8c;
        }
      }
    }

    .file-actions {
      display: flex;
      align-items: center;
      gap: 8px;

      .download-icon {
        color: #8c8c8c;
        transition: color 0.2s ease;
      }

      .delete-btn {
        min-width: 50px;
        height: 32px;
        font-size: 12px;
        border-radius: 4px;
      }
    }
  }
}

// 上传区域 - 与VantUploadImg完全一致
.upload-area {
  margin-bottom: 16px;

  :deep(.van-uploader) {
    .van-uploader__wrapper {
      display: flex;
      flex-direction: column;
      gap: 12px;
    }

    .van-uploader__upload {
      display: none; // 隐藏默认上传按钮
    }

    .van-uploader__preview {
      width: 80px;
      height: 80px;
      border-radius: 12px;
      overflow: hidden;
      margin: 0;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
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

// 文件预览优化
.file-preview-cover {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(0, 0, 0, 0.8) 0%, rgba(0, 0, 0, 0.6) 100%);
  color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  text-align: center;
  padding: 8px;
  backdrop-filter: blur(2px);

  .van-icon {
    margin-bottom: 6px;
  }

  .file-name {
    word-break: break-all;
    line-height: 1.2;
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    font-size: 9px;
    font-weight: 500;
  }
}

// 提示信息优化 - 超简版本
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
}

// 响应式优化 - 简化版本
@media (max-width: 375px) {
  .upload-title {
    font-size: 15px;
    margin-bottom: 14px;
  }

  .file-list {
    .file-item {
      padding: 10px;

      .file-info {
        .file-icon {
          margin-right: 10px;

          .van-icon {
            font-size: 18px;
          }
        }

        .file-details {
          .file-name {
            font-size: 13px;
          }

          .file-size {
            font-size: 11px;
          }
        }
      }

      .file-actions {
        .delete-btn {
          min-width: 45px;
          height: 28px;
          font-size: 11px;
        }
      }
    }
  }

  .upload-area {
    .custom-upload-area {
      padding: 14px;

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
  }
}
</style>
