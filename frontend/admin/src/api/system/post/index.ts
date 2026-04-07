import request from '@/config/axios'

export interface PostVO {
  id?: number
  name: string
  code: string
  sort: number
  status: number
  remark: string
  createTime?: Date
}

// 查询岗位列表
export const getPostPage = async (params: PageParam) => {
  return await request.get({ url: '/system/post/page', params })
}

// 获取岗位精简信息列表
export const getSimplePostList = async (): Promise<PostVO[]> => {
  return await request.get({ url: '/system/post/simple-list' })
}

// 查询岗位详情
export const getPost = async (id: number) => {
  return await request.get({ url: '/system/post/get?id=' + id })
}

// 新增岗位
export const createPost = async (data: PostVO) => {
  return await request.post({ url: '/system/post/create', data })
}

// 修改岗位
export const updatePost = async (data: PostVO) => {
  return await request.put({ url: '/system/post/update', data })
}

// 删除岗位
export const deletePost = async (id: number) => {
  return await request.delete({ url: '/system/post/delete?id=' + id })
}

// 批量删除岗位
export const deletePostList = async (ids: number[]) => {
  return await request.delete({ url: '/system/post/delete-list', params: { ids: ids.join(',') } })
}

// 导出岗位
export const exportPost = async (params) => {
  return await request.download({ url: '/system/post/export-excel', params })
}

/** 下载岗位导入模板 */
export const importTemplate = () => {
  return request.download({ url: '/system/post/get-import-template' })
}

/** 导入岗位 */
export const importPost = (data: FormData) => {
  return request.post({ 
    url: '/system/post/import',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 根据业务模块获取岗位列表
export const getPostListByBusinessModule = (moduleCode: string): Promise<PostVO[]> => {
  return request.get({ url: '/system/post/list-by-business-module', params: { moduleCode } })
}