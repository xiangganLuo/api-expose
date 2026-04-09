import request from '@/config/axios'

export interface ApexApiAssetVO {
  id: number
  name: string
  description?: string
  groupName: string
  protocolType: string
  status: string
  basePath: string
  createTime: string
  updateTime?: string
}

export interface ApexApiImportReqVO {
  groupName: string
  sourceType?: string
  fileContent: string
  sourceUrl?: string
  override?: boolean
}

export const importApiAsset = async (data: ApexApiImportReqVO): Promise<number> => {
  return await request.post({ url: '/apex/assets/import', data })
}

export const getApiAssetPage = async (params: any): Promise<{ list: ApexApiAssetVO[]; total: number }> => {
  return await request.get({ url: '/apex/assets/page', params })
}

export const getApiAssetDetail = async (id: number): Promise<ApexApiAssetVO> => {
  return await request.get({ url: `/apex/assets/get`, params: { id } })
}

export const updateApiAsset = async (data: any): Promise<boolean> => {
  return await request.put({ url: '/apex/assets/update', data })
}

export const deleteApiAsset = async (id: number): Promise<boolean> => {
  return await request.delete({ url: '/apex/assets/delete', params: { id } })
}

export const publishApiAsset = async (id: number): Promise<boolean> => {
  return await request.post({ url: `/apex/assets/publish`, params: { id } })
}

export const offlineApiAsset = async (id: number): Promise<boolean> => {
  return await request.post({ url: `/apex/assets/offline`, params: { id } })
}

export const deprecateApiAsset = async (id: number): Promise<boolean> => {
  return await request.post({ url: `/apex/assets/deprecate`, params: { id } })
}
