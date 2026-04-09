import request from '@/config/axios'

export interface ApexPolicyVO {
  id: number
  policyName: string
  scope: string
  apiAssetId?: number
  appId?: number
  enabled?: boolean
  rateLimitJson?: string
  circuitBreakerJson?: string
  accessControlJson?: string
  createTime?: string
  updateTime?: string
}

export interface ApexPolicyCreateReqVO {
  policyName: string
  scope: string
  apiAssetId?: number
  appId?: number
  enabled?: boolean
  rateLimitJson?: string
  circuitBreakerJson?: string
  accessControlJson?: string
}

export const createPolicy = async (data: ApexPolicyCreateReqVO): Promise<number> => {
  return await request.post({ url: '/apex/policies/create', data })
}

export const updatePolicy = async (data: any): Promise<boolean> => {
  return await request.put({ url: '/apex/policies/update', data })
}

export const deletePolicy = async (id: number): Promise<boolean> => {
  return await request.delete({ url: '/apex/policies/delete', params: { id } })
}

export const getPolicy = async (id: number): Promise<ApexPolicyVO> => {
  return await request.get({ url: '/apex/policies/get', params: { id } })
}

export const getPolicyPage = async (params: any): Promise<{ list: ApexPolicyVO[]; total: number }> => {
  return await request.get({ url: '/apex/policies/page', params })
}

export const updatePolicyStatus = async (id: number, enabled: boolean): Promise<boolean> => {
  return await request.put({ url: '/apex/policies/update-status', params: { id, enabled } })
}

