import type {Ref} from 'vue'
import {computed, ref, watch} from 'vue'
import {type DeptVO, getDeptListByBusinessModule} from '@/api/system/dept'
import {getPostListByBusinessModule, type PostVO} from '@/api/system/post'
import {getUserListByBusinessModule, type UserVO} from '@/api/system/user'

export interface BusinessModuleDataOptions {
  moduleCode: string
  enableDepts?: boolean
  enablePosts?: boolean
  enableUsers?: boolean
  autoLoad?: boolean
}

export interface BusinessModuleSelectOption {
  label: string
  value: number
  disabled?: boolean
}

/**
 * 业务模块数据查询组合式函数
 * 用于获取特定业务模块中实际使用的部门、岗位、用户数据
 *
 * @param options 配置选项
 * @returns 响应式数据和方法
 */
export function useBusinessModuleData(options: BusinessModuleDataOptions) {
  const {
    moduleCode,
    enableDepts = false,
    enablePosts = false,
    enableUsers = false,
    autoLoad = true
  } = options

  // 响应式数据
  const loading = ref(false)
  const depts = ref<DeptVO[]>([])
  const posts = ref<PostVO[]>([])
  const users = ref<UserVO[]>([])

  // 计算属性：转换为选择器需要的格式
  const deptOptions = computed<BusinessModuleSelectOption[]>(() =>
    depts.value.map(dept => ({
      label: dept.name,
      value: dept.id,
      disabled: dept.status !== 0
    }))
  )

  const postOptions = computed<BusinessModuleSelectOption[]>(() =>
    posts.value.map(post => ({
      label: post.name,
      value: post.id!,
      disabled: post.status !== 0
    }))
  )

  const userOptions = computed<BusinessModuleSelectOption[]>(() =>
    users.value.map(user => ({
      label: user.nickname || user.username,
      value: user.id,
      disabled: user.status !== 0
    }))
  )

  // 加载部门数据
  const loadDepts = async () => {
    if (!enableDepts) return
    try {
      loading.value = true
      const data = await getDeptListByBusinessModule(moduleCode)
      depts.value = data || []
    } catch (error) {
      console.error('Failed to load departments:', error)
      depts.value = []
    } finally {
      loading.value = false
    }
  }

  // 加载岗位数据
  const loadPosts = async () => {
    if (!enablePosts) return
    try {
      loading.value = true
      const data = await getPostListByBusinessModule(moduleCode)
      posts.value = data || []
    } catch (error) {
      console.error('Failed to load posts:', error)
      posts.value = []
    } finally {
      loading.value = false
    }
  }

  // 加载用户数据
  const loadUsers = async () => {
    if (!enableUsers) return
    try {
      loading.value = true
      const data = await getUserListByBusinessModule(moduleCode)
      users.value = data || []
    } catch (error) {
      console.error('Failed to load users:', error)
      users.value = []
    } finally {
      loading.value = false
    }
  }

  // 加载所有启用的数据
  const loadAll = async () => {
    const promises: Promise<void>[] = []

    if (enableDepts) promises.push(loadDepts())
    if (enablePosts) promises.push(loadPosts())
    if (enableUsers) promises.push(loadUsers())

    if (promises.length > 0) {
      loading.value = true
      try {
        await Promise.all(promises)
      } finally {
        loading.value = false
      }
    }
  }

  // 刷新所有数据
  const refresh = () => loadAll()

  // 监听模块编码变化，自动重新加载数据
  watch(() => moduleCode, loadAll, { immediate: autoLoad })

  // 根据ID查找对应的选项
  const getDeptOption = (id: number) => deptOptions.value.find(option => option.value === id)
  const getPostOption = (id: number) => postOptions.value.find(option => option.value === id)
  const getUserOption = (id: number) => userOptions.value.find(option => option.value === id)

  // 根据ID数组获取标签
  const getDeptLabels = (ids: number[]) => ids.map(id => getDeptOption(id)?.label).filter(Boolean)
  const getPostLabels = (ids: number[]) => ids.map(id => getPostOption(id)?.label).filter(Boolean)
  const getUserLabels = (ids: number[]) => ids.map(id => getUserOption(id)?.label).filter(Boolean)

  return {
    // 数据状态
    loading,
    depts,
    posts,
    users,

    // 选择器选项
    deptOptions,
    postOptions,
    userOptions,

    // 方法
    loadDepts,
    loadPosts,
    loadUsers,
    loadAll,
    refresh,

    // 工具方法
    getDeptOption,
    getPostOption,
    getUserOption,
    getDeptLabels,
    getPostLabels,
    getUserLabels
  }
}

/**
 * 创建业务模块数据查询hook的简化版本
 * 适用于只需要特定类型数据的场景
 */
export const useBusinessModuleDepts = (moduleCode: Ref<string> | string) =>
  useBusinessModuleData({
    moduleCode: typeof moduleCode === 'string' ? moduleCode : moduleCode.value,
    enableDepts: true
  })

export const useBusinessModulePosts = (moduleCode: Ref<string> | string) =>
  useBusinessModuleData({
    moduleCode: typeof moduleCode === 'string' ? moduleCode : moduleCode.value,
    enablePosts: true
  })

export const useBusinessModuleUsers = (moduleCode: Ref<string> | string) =>
  useBusinessModuleData({
    moduleCode: typeof moduleCode === 'string' ? moduleCode : moduleCode.value,
    enableUsers: true
  })

export default useBusinessModuleData
