/**
 * BPM业务表单相关类型定义
 */

// 业务表单标准接口
export interface IBpmBusinessForm {
  /**
   * 设置表单数据
   */
  setFormData(data: any): void
  
  /**
   * 获取表单数据
   */
  getFormData(): any
  
  /**
   * 验证表单
   */
  validate(): Promise<boolean>
  
  /**
   * 重置表单
   */
  resetForm(): void
  
  /**
   * 获取流程变量
   */
  getProcessVariables(): Record<string, any>
  
  /**
   * 设置流程变量
   */
  setProcessVariables(variables: Record<string, any>): void
  
  /**
   * 是否支持重新发起
   */
  supportRestart(): boolean
}

// 业务表单配置
export interface BusinessFormConfig {
  path: string
  processKey: string
  name: string
  supportRestart: boolean
}

// 重新发起参数
export interface RestartParams {
  isRestart: boolean
  processInstanceId?: string
  formData?: string
  processDefinitionId?: string
}
