import request from '@/config/axios'

export const getTrafficTrend = async (days: number): Promise<Record<string, number>> => {
  return await request.get({ url: '/apex/metrics/trend', params: { days } })
}

