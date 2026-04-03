<template>
  <el-select
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
    :placeholder="placeholder"
    :multiple="multiple"
    :filterable="filterable"
    :clearable="clearable"
    :loading="loading"
    :disabled="disabled"
    :size="size"
    :collapse-tags="multiple && collapseTags"
    :max-collapse-tags="maxCollapseTags"
    :reserve-keyword="reserveKeyword"
    :no-data-text="noDataText"
    :remote="remote"
    :remote-method="handleRemoteMethod"
    @change="handleChange"
    @clear="handleClear"
    @visible-change="handleVisibleChange"
    @focus="handleFocus"
    @blur="handleBlur"
    :style="$attrs.style"
    :class="$attrs.class"
  >
    <el-option
      v-for="option in finalOptions"
      :key="option.value"
      :label="option.label"
      :value="option.value"
      :disabled="option.disabled"
    />
  </el-select>
</template>

<script setup lang="ts">
import {computed, ref, watch} from 'vue'
import {type BusinessModuleSelectOption, useBusinessModuleData} from '@/hooks/web/useBusinessModuleData'

export interface BusinessModuleSelectorProps {
  modelValue?: number | number[]
  moduleCode: string
  type: 'dept' | 'post' | 'user'
  multiple?: boolean
  filterable?: boolean
  clearable?: boolean
  disabled?: boolean
  placeholder?: string
  size?: 'large' | 'default' | 'small'
  collapseTags?: boolean
  maxCollapseTags?: number
  reserveKeyword?: boolean
  noDataText?: string
  remote?: boolean
}

const props = withDefaults(defineProps<BusinessModuleSelectorProps>(), {
  multiple: false,
  filterable: true,
  clearable: true,
  disabled: false,
  placeholder: '请选择',
  size: 'default',
  collapseTags: true,
  maxCollapseTags: 3,
  reserveKeyword: false,
  noDataText: '暂无数据',
  remote: false
})

const emit = defineEmits<{
  'update:modelValue': [value: number | number[]]
  'change': [value: number | number[]]
  'clear': []
  'visible-change': [visible: boolean]
  'focus': [event: FocusEvent]
  'blur': [event: FocusEvent]
  'remote-method': [query: string]
}>()

// 使用业务模块数据查询hook
const businessModuleData = useBusinessModuleData({
  moduleCode: props.moduleCode,
  enableDepts: props.type === 'dept',
  enablePosts: props.type === 'post',
  enableUsers: props.type === 'user',
  autoLoad: true
})

// 根据类型获取对应的选项
const options = computed<BusinessModuleSelectOption[]>(() => {
  switch (props.type) {
    case 'dept':
      return businessModuleData.deptOptions.value
    case 'post':
      return businessModuleData.postOptions.value
    case 'user':
      return businessModuleData.userOptions.value
    default:
      return []
  }
})

// 实际渲染的选项（考虑远程搜索）
const finalOptions = computed<BusinessModuleSelectOption[]>(() => {
  const raw = props.remote ? remoteOptions.value : options.value
  // 不做禁用控制：下拉展示的都可选
  return raw.map(o => ({ ...o, disabled: false }))
})

// 加载状态
const loading = computed(() => businessModuleData.loading.value)

// 远程搜索相关
const remoteOptions = ref<BusinessModuleSelectOption[]>([])

// 监听模块编码变化，重新加载数据
watch(() => props.moduleCode, () => {
  businessModuleData.refresh()
})

// 事件处理
const handleChange = (value: number | number[]) => {
  emit('change', value)
}

const handleClear = () => {
  emit('clear')
}

const handleVisibleChange = (visible: boolean) => {
  emit('visible-change', visible)
}

const handleFocus = (event: FocusEvent) => {
  emit('focus', event)
}

const handleBlur = (event: FocusEvent) => {
  emit('blur', event)
}

const handleRemoteMethod = (query: string) => {
  if (!query) {
    remoteOptions.value = []
    return
  }

  // 过滤选项
  remoteOptions.value = options.value.filter(option =>
    option.label.toLowerCase().includes(query.toLowerCase())
  )

  emit('remote-method', query)
}

// 暴露方法供外部调用
defineExpose({
  refresh: businessModuleData.refresh,
  options: finalOptions,
  loading
})
</script>

<script lang="ts">
export default {
  name: 'BusinessModuleSelector',
  inheritAttrs: false
}
</script>
