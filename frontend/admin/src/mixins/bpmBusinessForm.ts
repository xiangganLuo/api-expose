/**
 * BPM业务表单通用混入
 * 提供业务表单重新发起功能的标准实现
 */

import {ref} from 'vue'
import {useRoute} from 'vue-router'
import {useMessage} from '@/hooks/web/useMessage'
import type {IBpmBusinessForm, RestartParams} from '@/types/bpm'
import {ElMessageBox} from 'element-plus'

export function useBpmBusinessForm() {
  const route = useRoute()
  const message = useMessage()

  // 通用状态
  const formLoading = ref(false)
  const isRestartMode = ref(false)
  const originalProcessInstanceId = ref('')
  const restartParams = ref<RestartParams>({
    isRestart: false
  })

  /**
   * 解析路由中的重新发起参数
   */
  const parseRestartParams = (): RestartParams => {
    const isRestart = route.query.isRestart === '1'
    const processInstanceId = route.query.processInstanceId as string
    const formData = route.query.formData as string
    const processDefinitionId = route.query.processDefinitionId as string

    // 增强判断：如果没有明确的 isRestart 参数，但有其他重新发起标识，也认为是重新发起
    const hasRestartIndicators = (
      processInstanceId &&                    // 有原流程实例ID
      (formData || processDefinitionId) &&   // 有表单数据或流程定义ID
      route.path.includes('/create')         // 在创建页面
    )

    console.log('[BPM] 重新发起参数解析:', {
      isRestart,
      processInstanceId: !!processInstanceId,
      hasFormData: !!formData,
      hasDefinitionId: !!processDefinitionId,
      hasRestartIndicators,
      finalIsRestart: isRestart || hasRestartIndicators
    })

    return {
      isRestart: Boolean(isRestart || hasRestartIndicators),  // 确保返回布尔值
      processInstanceId,
      formData,
      processDefinitionId
    }
  }

  /**
   * 创建业务表单接口实例
   */
  const createBusinessFormInterface = (
    formData: any,
    formRef: any,
    customHandlers?: Partial<IBpmBusinessForm>
  ): IBpmBusinessForm => {
    return {
      setFormData: (data: any) => {
        try {
          console.log('[BPM] 通用混入 setFormData 被调用:', data)
          if (data && formData.value) {
            console.log('[BPM] formData.value 当前状态:', formData.value)

            // 基础数据合并 - 逐个字段赋值确保响应式更新
            for (const key in data) {
              if (data.hasOwnProperty(key)) {
                const oldValue = formData.value[key]
                formData.value[key] = data[key]
                console.log(`[BPM] 设置字段 ${key}: ${oldValue} -> ${data[key]}`)

                // 验证是否设置成功
                if (formData.value[key] !== data[key]) {
                  console.warn(`[BPM] 字段 ${key} 设置失败！期望：${data[key]}，实际：${formData.value[key]}`)
                }
              }
            }
            console.log('[BPM] 基础数据合并完成:', formData.value)
          } else {
            console.warn('[BPM] 数据或formData为空:', { data, formDataValue: formData.value })
          }

          // 调用自定义处理器
          if (customHandlers?.setFormData) {
            console.log('[BPM] 调用自定义处理器')
            customHandlers.setFormData(data)
          }
        } catch (error) {
          console.error('[BPM] setFormData 过程中出错:', error)
        }
      },

      getFormData: () => {
        return customHandlers?.getFormData?.() || { ...formData.value }
      },

      validate: async () => {
        if (customHandlers?.validate) {
          return await customHandlers.validate()
        }
        if (!formRef.value) return false
        try {
          return await formRef.value.validate()
        } catch {
          return false
        }
      },

      resetForm: () => {
        if (customHandlers?.resetForm) {
          customHandlers.resetForm()
        } else {
          formRef.value?.resetFields()
        }
      },

      getProcessVariables: () => {
        return customHandlers?.getProcessVariables?.() || { ...formData.value }
      },

      setProcessVariables: (variables: Record<string, any>) => {
        if (customHandlers?.setProcessVariables) {
          return customHandlers.setProcessVariables(variables)
        }
        // 默认实现：直接设置表单数据
        return createBusinessFormInterface(formData, formRef, customHandlers).setFormData(variables)
      },

      supportRestart: () => {
        return customHandlers?.supportRestart?.() ?? true
      }
    }
  }

  /**
   * 处理重新发起逻辑
   */
  const handleRestartMode = async (
    businessFormInterface: IBpmBusinessForm,
    customDataHandler?: (data: any) => Promise<void>
  ) => {
    const params = parseRestartParams()
    restartParams.value = params

    if (params.isRestart && params.processInstanceId && params.formData) {
      try {
        isRestartMode.value = true
        originalProcessInstanceId.value = params.processInstanceId

        console.log('[BPM] 解析重新发起数据:', params.formData)
        const originalData = JSON.parse(decodeURIComponent(params.formData))
        console.log('[BPM] 解析后的原始数据:', originalData)

        // 验证数据有效性
        if (!originalData || typeof originalData !== 'object') {
          throw new Error('原始数据无效或为空')
        }

        if (customDataHandler) {
          console.log('[BPM] 使用自定义数据处理器')
          await customDataHandler(originalData)
        } else {
          console.log('[BPM] 使用标准接口设置数据')
          // 使用标准接口设置数据
          businessFormInterface.setProcessVariables(originalData)
        }

        message.info('已自动填充原流程的数据，您可以修改后重新提交')
        return originalData
      } catch (error: any) {
        console.error('[BPM] 解析重新发起数据失败:', error)
        message.error(`重新发起数据解析失败: ${error?.message || error}，请手动填写表单`)
        isRestartMode.value = false
      }
    }
    return null
  }

  /**
   * 提供数据选择策略（审批数据 vs 最新业务数据）
   */
  const handleDataSelectionStrategy = async (
    originalData: any,
    getLatestDataFn: (id: any) => Promise<any>
  ) => {
    if (!originalData.id || !getLatestDataFn) {
      return originalData
    }

    try {
      // 获取最新业务数据
      const latestBusinessData = await getLatestDataFn(originalData.id)

      // 比较数据是否有变化（简单比较关键字段）
      const hasChanged = JSON.stringify(originalData) !== JSON.stringify(latestBusinessData)

      if (!hasChanged) {
        return originalData
      }

      // 用户选择使用哪个数据
      const useLatest = await ElMessageBox.confirm(
        '检测到业务数据已更新，请选择：\n' +
        '• 确定：使用最新业务数据（推荐）\n' +
        '• 取消：使用原审批数据',
        '数据选择',
        {
          type: 'warning',
          confirmButtonText: '使用最新数据',
          cancelButtonText: '使用原审批数据'
        }
      )

      return useLatest ? latestBusinessData : originalData
    } catch (error) {
      // 用户取消或出错，使用原审批数据
      console.log('用户选择使用原审批数据或操作出错:', error)
      return originalData
    }
  }

  /**
   * 获取重新发起相关的提交参数
   */
  const getRestartSubmitParams = () => {
    if (!isRestartMode.value) return {}

    return {
      isRestart: true,
      originalProcessInstanceId: originalProcessInstanceId.value,
      restartComment: '重新发起审批'
    }
  }

  return {
    // 状态
    formLoading,
    isRestartMode,
    originalProcessInstanceId,
    restartParams,

    // 方法
    createBusinessFormInterface,
    handleRestartMode,
    handleDataSelectionStrategy,
    getRestartSubmitParams,
    parseRestartParams
  }
}

/**
 * 业务表单注册配置的常量
 */
export const BPM_BUSINESS_FORMS = {
  // HR模块
  HRM_RECRUIT_DEMAND: {
    createPath: '/bpm/hrm/recruitdemand/create',
    detailPath: '/bpm/hrm/recruitdemand/detail',
    processKey: 'hrm_recruit_demand',
    name: '招聘需求申请'
  },

  // OA模块
  OA_LEAVE: {
    createPath: '/bpm/oa/leave/create',
    detailPath: '/bpm/oa/leave/detail',
    processKey: 'oa_leave',
    name: '请假申请'
  }

  // 后续可添加更多业务表单配置
}
