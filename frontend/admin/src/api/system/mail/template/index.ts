import request from '@/config/axios'

export interface MailTemplateVO {
  id: number
  name: string
  code: string
  accountId: number
  nickname: string
  title: string
  content: string
  params: string
  status: number
  remark: string
}

// 邮件模板精简信息
export interface MailTemplateSimpleVO {
  id: number
  name: string
}

export interface MailSendReqVO {
  mail: string
  templateCode: string
  templateParams: Record<string, any>
  fileUrl?: string
  attachments?: File[]
}

// 查询邮件模版列表
export const getMailTemplatePage = async (params: PageParam) => {
  return await request.get({ url: '/system/mail-template/page', params })
}

// 查询邮件模版详情
export const getMailTemplate = async (id: number) => {
  return await request.get({ url: '/system/mail-template/get?id=' + id })
}

// 新增邮件模版
export const createMailTemplate = async (data: MailTemplateVO) => {
  return await request.post({ url: '/system/mail-template/create', data })
}

// 修改邮件模版
export const updateMailTemplate = async (data: MailTemplateVO) => {
  return await request.put({ url: '/system/mail-template/update', data })
}

// 删除邮件模版
export const deleteMailTemplate = async (id: number) => {
  return await request.delete({ url: '/system/mail-template/delete?id=' + id })
}

// 批量删除邮件模版
export const deleteMailTemplateList = async (ids: number[]) => {
  return await request.delete({ url: '/system/mail-template/delete-list', params: { ids: ids.join(',') } })
}

// 发送邮件
export const sendMail = (data: MailSendReqVO) => {
  return request.post({ url: '/system/mail-template/send-mail', data })
}

// 获得邮件模板精简列表
export const getSimpleMailTemplateList = async () => {
  return await request.get({ url: '/system/mail-template/simple-list' })
}

// 发送邮件（带附件）
export const sendMailWithAttachment = (data: MailSendReqVO) => {
  // 构建FormData来支持文件上传
  const formData = new FormData()
  formData.append('mail', data.mail)
  formData.append('templateCode', data.templateCode)
  
  // 处理模板参数
  if (data.templateParams && Object.keys(data.templateParams).length > 0) {
    formData.append('templateParams', JSON.stringify(data.templateParams))
  }
  
  // 处理附件文件
  if (data.attachments && data.attachments.length > 0) {
    data.attachments.forEach(file => {
      formData.append('attachments', file)
    })
  }
  
  return request.post({ 
    url: '/system/mail-template/send-mail-with-files', 
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
