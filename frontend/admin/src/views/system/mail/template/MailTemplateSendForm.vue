<template>
  <Dialog v-model="dialogVisible" title="测试">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="140px"
    >
      <el-form-item label="模板内容" prop="content">
        <Editor :model-value="formData.content" height="150px" readonly />
      </el-form-item>
      <el-form-item label="收件邮箱" prop="mail">
        <el-input v-model="formData.mail" placeholder="请输入收件邮箱" />
      </el-form-item>
      <el-form-item
        v-for="param in formData.params"
        :key="param"
        :label="'参数 {' + param + '}'"
        :prop="'templateParams.' + param"
      >
        <el-input
          v-model="formData.templateParams[param]"
          :placeholder="'请输入 ' + param + ' 参数'"
        />
      </el-form-item>
      <el-form-item label="附件" prop="attachments">
        <el-upload
          ref="uploadRef"
          v-model:file-list="fileList"
          action="#"
          :auto-upload="false"
          :on-change="handleFileChange"
          :on-remove="handleFileRemove"
          :before-upload="beforeUpload"
          multiple
          drag
        >
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
          <div class="el-upload__text">
            将文件拖到此处，或<em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              支持多个文件同时上传，文件大小不超过10MB
            </div>
          </template>
        </el-upload>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script lang="ts" setup>
import * as MailTemplateApi from '@/api/system/mail/template'
import {UploadFilled} from '@element-plus/icons-vue'
import type {UploadProps, UploadUserFile} from 'element-plus'

defineOptions({ name: 'SystemMailTemplateSendForm' })

const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const uploadRef = ref()
const fileList = ref<UploadUserFile[]>([])
const attachmentFiles = ref<File[]>([])

const formData = ref({
  content: '',
  params: [],
  mail: '',
  templateCode: '',
  templateParams: {}
})

const formRules = reactive({
  mail: [{ required: true, message: '邮箱不能为空', trigger: 'blur' }],
  templateCode: [{ required: true, message: '模版编号不能为空', trigger: 'blur' }],
  templateParams: {}
})
const formRef = ref() // 表单 Ref

// 文件上传处理
const handleFileChange: UploadProps['onChange'] = (uploadFile) => {
  if (uploadFile.raw) {
    attachmentFiles.value.push(uploadFile.raw)
  }
}

const handleFileRemove: UploadProps['onRemove'] = (uploadFile) => {
  const index = attachmentFiles.value.findIndex(file => file.name === uploadFile.name)
  if (index > -1) {
    attachmentFiles.value.splice(index, 1)
  }
}

const beforeUpload = (file: File) => {
  // 检查文件大小（10MB限制）
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    message.error('上传文件大小不能超过 10MB!')
    return false
  }
  return true
}

/** 打开弹窗 */
const open = async (id: number) => {
  dialogVisible.value = true
  resetForm()
  // 设置数据
  formLoading.value = true
  try {
    const data = await MailTemplateApi.getMailTemplate(id)
    // 设置动态表单
    formData.value.content = data.content
    formData.value.params = data.params
    formData.value.templateCode = data.code
    formData.value.templateParams = data.params.reduce((obj, item) => {
      obj[item] = '' // 给每个动态属性赋值，避免无法读取
      return obj
    }, {})
    formRules.templateParams = data.params.reduce((obj, item) => {
      obj[item] = { required: true, message: '参数 ' + item + ' 不能为空', trigger: 'blur' }
      return obj
    }, {})
  } finally {
    formLoading.value = false
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const submitForm = async () => {
  // 校验表单
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return

  // 提交请求
  formLoading.value = true
  try {
    const requestData: MailTemplateApi.MailSendReqVO = {
      mail: formData.value.mail,
      templateCode: formData.value.templateCode,
      templateParams: formData.value.templateParams,
      attachments: attachmentFiles.value
    }

    let logId
    if (attachmentFiles.value.length > 0) {
      // 有附件时使用带附件的接口
      logId = await MailTemplateApi.sendMailWithAttachment(requestData)
    } else {
      // 无附件时使用普通接口
      logId = await MailTemplateApi.sendMail(requestData)
    }

    if (logId) {
      message.success('提交发送成功！发送结果，见发送日志编号：' + logId)
    }
    dialogVisible.value = false
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    content: '',
    params: [],
    mail: '',
    templateCode: '',
    templateParams: {}
  }
  formRules.templateParams = {}
  fileList.value = []
  attachmentFiles.value = []
  formRef.value?.resetFields()
}
</script>
