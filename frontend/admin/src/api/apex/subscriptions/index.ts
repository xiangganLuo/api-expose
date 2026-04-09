import request from '@/config/axios'

export interface ApexSubscriptionVO {
  id: number
  appId: number
  apiAssetId: number
  status?: string
  remark?: string
  createTime?: string
}

export interface ApexSubscriptionCreateReqVO {
  appId: number
  apiAssetId: number
  remark?: string
  status?: string
}

export const createSubscription = async (data: ApexSubscriptionCreateReqVO): Promise<number> => {
  return await request.post({ url: '/apex/subscriptions/create', data })
}

export const approveSubscription = async (id: number): Promise<boolean> => {
  return await request.put({ url: '/apex/subscriptions/approve', params: { id } })
}

export const rejectSubscription = async (id: number, remark?: string): Promise<boolean> => {
  return await request.put({ url: '/apex/subscriptions/reject', params: { id, remark } })
}

export const getSubscriptionPage = async (
  params: any
): Promise<{ list: ApexSubscriptionVO[]; total: number }> => {
  return await request.get({ url: '/apex/subscriptions/page', params })
}

