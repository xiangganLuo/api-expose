import request from '@/config/axios'

export interface DataPermissionConfigVO {
  id?: number
  configKey: string
  configName: string
  businessType: string
  roleIds: number[]
  filterColumn: string
  filterTable?: string
  status: number
  remark?: string
  createTime?: Date
  creator?: string
  updateTime?: Date
  updater?: string
}

export interface DataPermissionConfigPageReqVO {
  pageNo?: number
  pageSize?: number
  configKey?: string
  configName?: string
  businessType?: string
  status?: number
}

// 查询数据权限配置分页
export const getDataPermissionConfigPage = (params: DataPermissionConfigPageReqVO) => {
  return request.get({ url: `/system/data-permission-config/page`, params })
}

// 查询数据权限配置详情
export const getDataPermissionConfig = (id: number) => {
  return request.get({ url: `/system/data-permission-config/get?id=` + id })
}

// 查询数据权限配置列表
export const getDataPermissionConfigList = () => {
  return request.get({ url: `/system/data-permission-config/list` })
}

// 新增数据权限配置
export const createDataPermissionConfig = (data: DataPermissionConfigVO) => {
  return request.post({ url: `/system/data-permission-config/create`, data })
}

// 修改数据权限配置
export const updateDataPermissionConfig = (data: DataPermissionConfigVO) => {
  return request.put({ url: `/system/data-permission-config/update`, data })
}

// 删除数据权限配置
export const deleteDataPermissionConfig = (id: number) => {
  return request.delete({ url: `/system/data-permission-config/delete?id=` + id })
}

// 导出数据权限配置 Excel
export const exportDataPermissionConfigExcel = (params: DataPermissionConfigPageReqVO) => {
  return request.download({ url: `/system/data-permission-config/export-excel`, params })
}
