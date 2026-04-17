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
  sourceType: 'OPENAPI_CONTENT' | 'OPENAPI_URL'
  fileContent: string
  sourceUrl?: string
  override?: boolean
}

export interface ApexApiEndpointRespVO {
  id: number
  assetId: number
  path: string
  httpMethod: string
  name: string
  summary: string
  requestSchema: string
  responseSchema: string
  upstreamUrl: string
  timeoutMs: number
}

export interface ApexApiVersionRespVO {
  id: number
  assetId: number
  version: string
  active: number
  releaseNote: string
  createTime: string
}

export interface ApexApiTryReqVO {
  assetId: number
  envCode?: string
  endpointPath: string
  httpMethod: string
  headers?: Record<string, string>
  body?: string
}

export interface ApexAssetEnvRespVO {
  id: number
  assetId: number
  envCode: string
  envName: string
  baseUrl: string
  status: number
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

export const publishApiAsset = async (id: number, envCode: string): Promise<boolean> => {
  return await request.post({ url: `/apex/assets/publish`, params: { id, envCode } })
}

export const offlineApiAsset = async (id: number): Promise<boolean> => {
  return await request.post({ url: `/apex/assets/offline`, params: { id } })
}

export const deprecateApiAsset = async (id: number): Promise<boolean> => {
  return await request.post({ url: `/apex/assets/deprecate`, params: { id } })
}

export const tryApiAsset = async (data: ApexApiTryReqVO): Promise<any> => {
  return await request.postOriginal({ url: '/apex/assets/try', data, responseType: 'arraybuffer' })
}

// 查询端点列表
export const getEndpoints = (assetId: number) => {
  return request.get({ url: `/apex/assets/endpoints/list?assetId=${assetId}` })
}

// 保存端点
export const saveEndpoint = (data: any) => {
  return request.post({ url: '/apex/assets/endpoints/save', data })
}

// 删除端点
export const deleteEndpoint = (id: number) => {
  return request.delete({ url: `/apex/assets/endpoints/delete?id=${id}` })
}

// 查询版本记录
export const getVersions = (assetId: number) => {
  return request.get({ url: `/apex/assets/versions/list?assetId=${assetId}` })
}

// ====== 资产环境管理 ======

export const getAssetEnvs = (assetId: number) => {
  return request.get({ url: `/apex/assets/envs/list?assetId=${assetId}` })
}

export const saveAssetEnv = (data: any) => {
  return request.post({ url: '/apex/assets/envs/save', data })
}

export const deleteAssetEnv = (id: number) => {
  return request.delete({ url: `/apex/assets/envs/delete?id=${id}` })
}
