import request from '@/config/axios'

export interface ApexDeveloperAppVO {
  id: number
  name: string
  description?: string
  status?: string
  apiKey?: string
  callbackUrl?: string
  createTime?: string
  updateTime?: string
}

export interface ApexDeveloperAppCreateReqVO {
  name: string
  description?: string
  callbackUrl?: string
  status?: string
}

export interface ApexDeveloperAppCreateRespVO {
  appId: number
  apiKey: string
  apiSecret: string
}

export const createApp = async (data: ApexDeveloperAppCreateReqVO): Promise<ApexDeveloperAppCreateRespVO> => {
  return await request.post({ url: '/apex/apps/create', data })
}

export const updateApp = async (data: any): Promise<boolean> => {
  return await request.put({ url: '/apex/apps/update', data })
}

export const deleteApp = async (id: number): Promise<boolean> => {
  return await request.delete({ url: '/apex/apps/delete', params: { id } })
}

export const getApp = async (id: number): Promise<ApexDeveloperAppVO> => {
  return await request.get({ url: '/apex/apps/get', params: { id } })
}

export const getAppPage = async (params: any): Promise<{ list: ApexDeveloperAppVO[]; total: number }> => {
  return await request.get({ url: '/apex/apps/page', params })
}

