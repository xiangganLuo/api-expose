/**
 * BPM业务表单管理器
 * 统一管理所有业务表单的注册、查找和操作
 */

import type {BusinessFormConfig, IBpmBusinessForm} from '@/types/bpm'

export class BusinessFormManager {
  private static instance: BusinessFormManager

  /** 业务表单实例注册表 */
  private formInstances = new Map<string, IBpmBusinessForm>()

  /** 业务表单配置注册表 */
  private formConfigs = new Map<string, BusinessFormConfig>()

  private constructor() {}

  /**
   * 获取单例实例
   */
  static getInstance(): BusinessFormManager {
    if (!BusinessFormManager.instance) {
      BusinessFormManager.instance = new BusinessFormManager()
    }
    return BusinessFormManager.instance
  }

  /**
   * 注册业务表单配置
   */
  registerFormConfig(config: BusinessFormConfig): void {
    this.formConfigs.set(config.path, config)
  }

  /**
   * 注册业务表单实例
   */
  registerFormInstance(formPath: string, formInstance: IBpmBusinessForm): void {
    if (formInstance) {
      this.formInstances.set(formPath, formInstance)
      console.log(`[BPM] 注册业务表单实例: ${formPath}`)
    }
  }

  /**
   * 注销业务表单实例
   */
  unregisterFormInstance(formPath: string): void {
    this.formInstances.delete(formPath)
    console.log(`[BPM] 注销业务表单实例: ${formPath}`)
  }

  /**
   * 获取业务表单配置
   */
  getFormConfig(formPath: string): BusinessFormConfig | undefined {
    return this.formConfigs.get(formPath)
  }

  /**
   * 获取业务表单实例
   */
  getFormInstance(formPath: string): IBpmBusinessForm | undefined {
    return this.formInstances.get(formPath)
  }

  /**
   * 检查表单是否支持重新发起
   */
  supportsRestart(formPath: string): boolean {
    // 首先检查配置
    const config = this.formConfigs.get(formPath)
    if (config && config.supportRestart !== undefined) {
      return config.supportRestart
    }

    // 然后检查实例
    const instance = this.formInstances.get(formPath)
    if (instance) {
      return instance.supportRestart()
    }

    // 默认不支持
    return false
  }

  /**
   * 获取所有支持重新发起的表单路径
   */
  getRestartSupportedForms(): string[] {
    const supportedForms: string[] = []

    // 从配置中查找
    for (const [path, config] of this.formConfigs.entries()) {
      if (config.supportRestart) {
        supportedForms.push(path)
      }
    }

    // 从实例中查找（补充）
    for (const [path, instance] of this.formInstances.entries()) {
      if (!supportedForms.includes(path) && instance.supportRestart()) {
        supportedForms.push(path)
      }
    }

    return supportedForms
  }

  /**
   * 统一处理重新发起
   */
  async handleRestart(formPath: string, formData: any): Promise<boolean> {
    const formInstance = this.getFormInstance(formPath)
    if (!formInstance) {
      throw new Error(`业务表单实例未找到: ${formPath}`)
    }

    if (!formInstance.supportRestart()) {
      throw new Error(`业务表单不支持重新发起: ${formPath}`)
    }

    try {
      formInstance.setProcessVariables(formData)
      return true
    } catch (error) {
      console.error('重新发起数据设置失败:', error)
      return false
    }
  }

  /**
   * 验证业务表单
   */
  async validateForm(formPath: string): Promise<boolean> {
    const formInstance = this.getFormInstance(formPath)
    if (!formInstance) {
      console.warn(`业务表单实例未找到: ${formPath}`)
      return false
    }

    return await formInstance.validate()
  }

  /**
   * 获取业务表单数据
   */
  getFormData(formPath: string): any {
    const formInstance = this.getFormInstance(formPath)
    if (!formInstance) {
      console.warn(`业务表单实例未找到: ${formPath}`)
      return null
    }

    return formInstance.getFormData()
  }

  /**
   * 获取流程变量
   */
  getProcessVariables(formPath: string): Record<string, any> {
    const formInstance = this.getFormInstance(formPath)
    if (!formInstance) {
      console.warn(`业务表单实例未找到: ${formPath}`)
      return {}
    }

    return formInstance.getProcessVariables()
  }

  /**
   * 获取所有已注册的表单配置
   */
  getAllFormConfigs(): BusinessFormConfig[] {
    return Array.from(this.formConfigs.values())
  }

  /**
   * 根据流程定义Key查找表单配置
   */
  getFormConfigByProcessKey(processKey: string): BusinessFormConfig | undefined {
    for (const config of this.formConfigs.values()) {
      if (config.processKey === processKey) {
        return config
      }
    }
    return undefined
  }

  /**
   * 清理所有注册的表单实例（主要用于测试或重置）
   */
  clearAll(): void {
    this.formInstances.clear()
    this.formConfigs.clear()
    console.log('[BPM] 清理所有业务表单注册')
  }

  /**
   * 获取管理器状态信息（用于调试）
   */
  getStatus(): {
    configCount: number
    instanceCount: number
    configs: string[]
    instances: string[]
  } {
    return {
      configCount: this.formConfigs.size,
      instanceCount: this.formInstances.size,
      configs: Array.from(this.formConfigs.keys()),
      instances: Array.from(this.formInstances.keys())
    }
  }
}

/**
 * 预定义的业务表单配置注册
 */
export const initDefaultBusinessForms = () => {
  const manager = BusinessFormManager.getInstance()

  // 注册招聘需求申请
  manager.registerFormConfig({
    path: '/bpm/hrm/recruitdemand/create',
    processKey: 'hrm_recruit_demand',
    name: '招聘需求申请',
    supportRestart: true
  })

  // 注册招聘需求申请（数据库实际路径）
  manager.registerFormConfig({
    path: '/hrm/recruitdemand/create',
    processKey: 'hrm_recruit_demand',
    name: '招聘需求申请',
    supportRestart: true
  })

  // 注册请假申请
  manager.registerFormConfig({
    path: '/bpm/oa/leave/create',
    processKey: 'oa_leave',
    name: '请假申请',
    supportRestart: true
  })

  // 注册请假申请（数据库实际路径）
  manager.registerFormConfig({
    path: '/oa/leave/create',
    processKey: 'oa_leave',
    name: '请假申请',
    supportRestart: true
  })

  // 注册员工入职审批
  manager.registerFormConfig({
    path: '/bpm/hrm/employeeonboard/create',
    processKey: 'hrm_employee_onboard',
    name: '员工入职审批',
    supportRestart: true
  })

  // 注册员工入职审批（数据库实际路径）
  manager.registerFormConfig({
    path: '/hrm/employeeonboard/create',
    processKey: 'hrm_employee_onboard',
    name: '员工入职审批',
    supportRestart: true
  })

  console.log('[BPM] 已注册的配置列表:', manager.getAllFormConfigs().map(config => config.path))
}

// 导出单例实例
export const businessFormManager = BusinessFormManager.getInstance()
