# BPM 业务表单重新发起解决方案

## 概述

本方案提供了一套完整的 BPM 业务表单重新发起功能，支持业务表单在流程被拒绝后可以重新发起。

## 方案特点

1. **标准化接口**：定义了统一的 `IBpmBusinessForm` 接口，所有业务表单都遵循同一套规范
2. **可复用组件**：通过 `useBpmBusinessForm` 混入，新的业务表单可以快速集成
3. **统一管理**：`BusinessFormManager` 提供全局的业务表单管理功能
4. **向后兼容**：现有的业务表单可以逐步升级支持重新发起
5. **类型安全**：使用 TypeScript 接口保证类型安全

## 核心文件

```
src/
├── types/bpm.ts                          # BPM 相关类型定义
├── mixins/bpmBusinessForm.ts              # 通用业务表单混入
├── utils/bpm/
│   ├── businessFormManager.ts            # 业务表单管理器
│   └── README.md                         # 本文档
├── plugins/bpm.ts                        # BPM 插件初始化
└── views/bpm/
    ├── processInstance/index.vue         # 流程实例管理（移除限制）
    ├── processInstance/create/ProcessDefinitionDetail.vue  # 增强数据传递
    ├── hrm/recruitdemand/create.vue      # 招聘需求申请（示例）
    └── oa/leave/create.vue               # 请假申请（示例）
```

## 快速开始

### 1. 为现有业务表单添加重新发起支持

以请假申请为例：

```vue
<script setup lang="ts">
// 1. 导入必要的模块
import { useBpmBusinessForm } from '@/mixins/bpmBusinessForm'
import { businessFormManager } from '@/utils/bpm/businessFormManager'
import type { IBpmBusinessForm } from '@/types/bpm'

// 2. 使用混入
const {
  isRestartMode,
  createBusinessFormInterface,
  handleRestartMode,
  getRestartSubmitParams
} = useBpmBusinessForm()

// 3. 创建业务表单接口
const businessFormInterface: IBpmBusinessForm = createBusinessFormInterface(
  formData,
  formRef,
  {
    // 自定义处理器（可选）
    setFormData: (data: any) => {
      // 特定的数据处理逻辑
      if (data.startTime) {
        formData.value.startTime = new Date(data.startTime).getTime()
      }
    },
    
    getProcessVariables: () => ({
      ...formData.value,
      // 额外的流程变量
      duration: calculateDuration(formData.value.startTime, formData.value.endTime)
    }),
    
    supportRestart: () => true
  }
)

// 4. 在 onMounted 中注册和处理重新发起
onMounted(async () => {
  // 注册到管理器
  businessFormManager.registerFormInstance('/bpm/oa/leave/create', businessFormInterface)
  
  // 处理重新发起
  await handleRestartMode(businessFormInterface)
})

// 5. 在 onUnmounted 中注销
onUnmounted(() => {
  businessFormManager.unregisterFormInstance('/bpm/oa/leave/create')
})

// 6. 暴露接口
defineExpose(businessFormInterface)
</script>
```

### 2. 创建新的业务表单

新建业务表单时，只需按照上述模板进行配置即可自动获得重新发起支持。

### 3. 注册业务表单配置

在 `src/utils/bpm/businessFormManager.ts` 的 `initDefaultBusinessForms` 函数中添加：

```typescript
// 注册新的业务表单
manager.registerFormConfig({
  path: '/bpm/your-module/your-form/create',
  processKey: 'your_process_key',
  name: '您的表单名称',
  supportRestart: true
})
```

## API 参考

### IBpmBusinessForm 接口

```typescript
interface IBpmBusinessForm {
  setFormData(data: any): void                    // 设置表单数据
  getFormData(): any                              // 获取表单数据
  validate(): Promise<boolean>                    // 验证表单
  resetForm(): void                               // 重置表单
  getProcessVariables(): Record<string, any>      // 获取流程变量
  setProcessVariables(variables: Record<string, any>): void  // 设置流程变量
  supportRestart(): boolean                       // 是否支持重新发起
}
```

### useBpmBusinessForm 混入

```typescript
const {
  isRestartMode,                    // 是否为重新发起模式
  originalProcessInstanceId,        // 原流程实例ID
  createBusinessFormInterface,      // 创建业务表单接口
  handleRestartMode,               // 处理重新发起逻辑
  handleDataSelectionStrategy,     // 数据选择策略
  getRestartSubmitParams           // 获取重新发起提交参数
} = useBpmBusinessForm()
```

### BusinessFormManager 管理器

```typescript
// 注册表单配置
businessFormManager.registerFormConfig(config)

// 注册表单实例
businessFormManager.registerFormInstance(path, instance)

// 检查是否支持重新发起
businessFormManager.supportsRestart(path)

// 获取表单实例
businessFormManager.getFormInstance(path)
```

## 工作流程

1. **用户点击重新发起**：在流程实例列表页面点击"重新发起"按钮
2. **系统检查支持性**：通过 `BusinessFormManager` 检查该业务表单是否支持重新发起
3. **数据传递**：系统将原流程的 `formVariables` 数据编码后传递给业务表单页面
4. **数据回填**：业务表单页面使用 `handleRestartMode` 解析数据并回填到表单
5. **用户修改提交**：用户可以修改表单数据后重新提交

## 数据流转

```
流程实例 -> formVariables -> 编码传递 -> 业务表单页面 -> 解码回填 -> 用户修改 -> 重新提交
```

## 最佳实践

1. **数据处理**：在 `setFormData` 中处理特定的数据格式转换
2. **验证逻辑**：在 `validate` 中添加业务特定的验证规则
3. **流程变量**：在 `getProcessVariables` 中提供流程引擎需要的额外变量
4. **错误处理**：合理处理重新发起数据解析失败的情况
5. **用户体验**：提供明确的重新发起状态提示

## 故障排除

### 1. 重新发起按钮被禁用
- 检查 `BusinessFormManager` 中是否注册了对应的表单配置
- 确认 `supportRestart` 返回 `true`

### 2. 数据回填失败
- 检查 `formData` 的编码/解码是否正确
- 确认 `setFormData` 方法的实现是否正确

### 3. 类型错误
- 确保导入的接口类型正确
- 检查 `formData` 和相关 ref 的类型定义

## 扩展指南

如需添加新的功能或修改现有行为，可以：

1. **扩展接口**：在 `IBpmBusinessForm` 中添加新方法
2. **增强混入**：在 `useBpmBusinessForm` 中添加新功能
3. **自定义管理器**：扩展 `BusinessFormManager` 的功能
4. **添加钩子**：在关键节点添加生命周期钩子

## 版本历史

- v1.0.0: 初始版本，支持基础重新发起功能
- 后续版本将根据需求进行功能增强

## 贡献指南

欢迎提交 Issue 和 Pull Request 来改进这个方案。
