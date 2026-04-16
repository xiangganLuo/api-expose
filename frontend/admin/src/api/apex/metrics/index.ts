import request from '@/config/axios'

export interface ApexMetricPointVO {
  time: string
  calls: number
  successCalls: number
  failCalls: number
  avgLatencyMs: number
}

export interface ApexMetricRespVO {
  totalCalls: number
  successCalls: number
  failCalls: number
  avgLatencyMs: number
  series: ApexMetricPointVO[]
}

export const getTrafficTrend = async (days: number): Promise<Record<string, number>> => {
  return await request.get({ url: '/apex/metrics/trend', params: { days } })
}

export const getMetricsByApp = async (params: {
  appId: number
  startTime: string
  endTime: string
  granularity?: string
}): Promise<ApexMetricRespVO> => {
  return await request.get({ url: '/apex/metrics/app', params })
}

export const getMetricsByAsset = async (params: {
  apiAssetId: number
  startTime: string
  endTime: string
  granularity?: string
}): Promise<ApexMetricRespVO> => {
  return await request.get({ url: '/apex/metrics/asset', params })
}
