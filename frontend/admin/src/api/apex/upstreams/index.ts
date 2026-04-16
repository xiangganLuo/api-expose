import request from '@/config/axios'

export interface EnvironmentVO {
  id?: number
  code: string
  name: string
  sort?: number
  status: number
}

export interface UpstreamConfigVO {
  envId: number
  envCode?: string
  envName?: string
  baseUrl: string
}

export interface UpstreamVO {
  id?: number
  name: string
  description?: string
  configs?: UpstreamConfigVO[]
}

// 运行环境相关
export const getEnvironmentList = () => {
  return request.get({ url: '/apex/environments/list' })
}

export const saveEnvironment = (data: EnvironmentEntity) => {
  return request.post({ url: '/apex/environments/save', data })
}

export const deleteEnvironment = (id: number) => {
  return request.delete({ url: `/apex/environments/delete?id=${id}` })
}

// 上游服务相关
export const getUpstreamList = () => {
  return request.get({ url: '/apex/upstreams/list' })
}

export const getUpstream = (id: number) => {
  return request.get({ url: `/apex/upstreams/get?id=${id}` })
}

export const saveUpstream = (data: UpstreamVO) => {
  return request.post({ url: '/apex/upstreams/save', data })
}

export const deleteUpstream = (id: number) => {
  return request.delete({ url: `/apex/upstreams/delete?id=${id}` })
}
