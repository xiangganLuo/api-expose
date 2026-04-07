import request from '@/config/axios'

export interface DeptVO {
  id: number
  name: string
  parentId: number
  status: number
  sort: number
  leaderUserId: number
  phone: string
  email: string
  createTime: Date
  deptLevelName: string // 部门层级全称
  deptType: string      // 部门类型
}

// 查询部门（精简)列表
export const getSimpleDeptList = (): Promise<DeptVO[]> => {
  return request.get({ url: '/system/dept/simple-list' })
}

// 查询部门列表
export const getDeptList = (params: any) => {
  return request.get({ url: '/system/dept/list', params })
}

// 查询部门分页
export const getDeptPage = async (params: PageParam) => {
  return await request.get({ url: '/system/dept/list', params })
}

// 查询部门详情
export const getDept = (id: number) => {
  return request.get({ url: '/system/dept/get?id=' + id })
}

// 新增部门
export const createDept = (data: DeptVO) => {
  return request.post({ url: '/system/dept/create', data })
}

// 修改部门
export const updateDept = (data: DeptVO) => {
  return request.put({ url: '/system/dept/update', data })
}

// 删除部门
export const deleteDept = async (id: number) => {
  return await request.delete({ url: '/system/dept/delete?id=' + id })
}

// 批量删除部门
export const deleteDeptList = async (ids: number[]) => {
  return await request.delete({ url: '/system/dept/delete-list', params: { ids: ids.join(',') } })
}

/** 导出部门 */
export const exportDept = (params: any) => {
  return request.download({ url: '/system/dept/export-excel', params })
}

// 根据业务模块获取部门列表
export const getDeptListByBusinessModule = (moduleCode: string): Promise<DeptVO[]> => {
  return request.get({ url: '/system/dept/list-by-business-module', params: { moduleCode } })
}

// 修复部门层级全称
export const fixDeptLevelNames = () => {
  return request.post({ url: '/system/dept/fix-dept-level-names' })
}