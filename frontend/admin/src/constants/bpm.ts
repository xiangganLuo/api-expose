/**
 * BPM 流程定义 Key 枚举
 * 用于统一管理所有业务流程的流程标识
 */
export const BPM_PROCESS_KEYS = {
  // OA 相关流程
  OA_LEAVE: 'oa_leave',
  
  // HRM 相关流程
  HRM_RECRUIT_DEMAND: 'hrm_recruit_demand',
  HRM_EMPLOYEE_ONBOARD: 'hrm_employee_onboard',
  HRM_EMPLOYEE_OFFBOARD: 'hrm_employee_offboard',
  HRM_SALARY_ADJUST: 'hrm_salary_adjust',
  HRM_PERFORMANCE_REVIEW: 'hrm_performance_review',
  
  // CRM 相关流程
  CRM_CONTRACT_APPROVAL: 'crm_contract_approval',
  CRM_RECEIVABLE_APPROVAL: 'crm_receivable_approval',
  CRM_BUSINESS_APPROVAL: 'crm_business_approval',
  
  // ERP 相关流程
  ERP_PURCHASE_APPROVAL: 'erp_purchase_approval',
  ERP_EXPENSE_APPROVAL: 'erp_expense_approval',
  ERP_BUDGET_APPROVAL: 'erp_budget_approval',
  
  // 财务相关流程
  FIN_PAYMENT_APPROVAL: 'fin_payment_approval',
  FIN_INVOICE_APPROVAL: 'fin_invoice_approval',
  
  // 其他业务流程
  PROJECT_APPROVAL: 'project_approval',
  ASSET_APPROVAL: 'asset_approval',
  TRAVEL_APPROVAL: 'travel_approval',
} as const

/**
 * BPM 流程定义 Key 类型
 */
export type BpmProcessKey = typeof BPM_PROCESS_KEYS[keyof typeof BPM_PROCESS_KEYS]

/**
 * 获取流程 Key 的描述信息
 */
export const getProcessKeyDescription = (key: BpmProcessKey): string => {
  const descriptions: Record<BpmProcessKey, string> = {
    [BPM_PROCESS_KEYS.OA_LEAVE]: 'OA 请假流程',
    [BPM_PROCESS_KEYS.HRM_RECRUIT_DEMAND]: '招聘需求审批流程',
    [BPM_PROCESS_KEYS.HRM_EMPLOYEE_ONBOARD]: '员工入职流程',
    [BPM_PROCESS_KEYS.HRM_EMPLOYEE_OFFBOARD]: '员工离职流程',
    [BPM_PROCESS_KEYS.HRM_SALARY_ADJUST]: '薪资调整流程',
    [BPM_PROCESS_KEYS.HRM_PERFORMANCE_REVIEW]: '绩效评估流程',
    [BPM_PROCESS_KEYS.CRM_CONTRACT_APPROVAL]: '合同审批流程',
    [BPM_PROCESS_KEYS.CRM_RECEIVABLE_APPROVAL]: '回款审批流程',
    [BPM_PROCESS_KEYS.CRM_BUSINESS_APPROVAL]: '商机审批流程',
    [BPM_PROCESS_KEYS.ERP_PURCHASE_APPROVAL]: '采购审批流程',
    [BPM_PROCESS_KEYS.ERP_EXPENSE_APPROVAL]: '费用审批流程',
    [BPM_PROCESS_KEYS.ERP_BUDGET_APPROVAL]: '预算审批流程',
    [BPM_PROCESS_KEYS.FIN_PAYMENT_APPROVAL]: '付款审批流程',
    [BPM_PROCESS_KEYS.FIN_INVOICE_APPROVAL]: '发票审批流程',
    [BPM_PROCESS_KEYS.PROJECT_APPROVAL]: '项目审批流程',
    [BPM_PROCESS_KEYS.ASSET_APPROVAL]: '资产审批流程',
    [BPM_PROCESS_KEYS.TRAVEL_APPROVAL]: '出差审批流程',
  }
  return descriptions[key] || '未知流程'
} 