<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible" width="600px">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      v-loading="formLoading"
    >
      <el-form-item label="配置键" prop="configKey">
        <el-input v-model="formData.configKey" placeholder="请输入配置键，如: candidate_dept_filter" />
      </el-form-item>
      <el-form-item label="配置名称" prop="configName">
        <el-input v-model="formData.configName" placeholder="请输入配置名称，如: 候选人部门权限过滤" />
      </el-form-item>
      <el-form-item label="业务类型" prop="businessType">
        <el-input v-model="formData.businessType" placeholder="请输入业务类型，如: candidate" />
      </el-form-item>
      <el-form-item label="受限角色" prop="roleIds">
        <el-select
          v-model="formData.roleIds"
          placeholder="请选择受限制的角色"
          multiple
          style="width: 100%"
        >
          <el-option
            v-for="role in roleOptions"
            :key="role.id"
            :label="role.name"
            :value="role.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="过滤字段" prop="filterColumn">
        <el-input v-model="formData.filterColumn" placeholder="请输入过滤字段名，如: dept_id" />
      </el-form-item>
      <el-form-item label="过滤表名" prop="filterTable">
        <el-input v-model="formData.filterTable" placeholder="请输入过滤表名（跨表时使用），如: system_user" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="formData.remark"
          type="textarea"
          placeholder="请输入备注"
          :rows="3"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import * as DataPermissionConfigApi from '@/api/system/dataPermissionConfig'
import {DataPermissionConfigVO} from '@/api/system/dataPermissionConfig'
import {getSimpleRoleList, RoleVO} from '@/api/system/role'
import {DICT_TYPE, getIntDictOptions} from '@/utils/dict'

/** 数据权限配置 表单 */
defineOptions({ name: 'DataPermissionConfigForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  configKey: '',
  configName: '',
  businessType: '',
  roleIds: [],
  filterColumn: '',
  filterTable: '',
  status: 0, // 默认开启状态 (CommonStatusEnum.ENABLE = 0，从字典动态获取)
  remark: ''
})
const formRules = reactive({
  configKey: [{ required: true, message: '配置键不能为空', trigger: 'blur' }],
  configName: [{ required: true, message: '配置名称不能为空', trigger: 'blur' }],
  businessType: [{ required: true, message: '业务类型不能为空', trigger: 'blur' }],
  roleIds: [{ required: true, message: '受限角色不能为空', trigger: 'change' }],
  filterColumn: [{ required: true, message: '过滤字段名不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'change' }]
})
const formRef = ref() // 表单 Ref

// 角色选项
const roleOptions = ref<RoleVO[]>([])

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()

  // 加载角色选项
  await loadRoleOptions()

  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await DataPermissionConfigApi.getDataPermissionConfig(id)
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 加载角色选项 */
const loadRoleOptions = async () => {
  try {
    roleOptions.value = await getSimpleRoleList()
  } catch {
    // 加载失败不影响表单功能
    roleOptions.value = []
  }
}

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 校验表单
  await formRef.value.validate()
  // 提交请求
  formLoading.value = true
  try {
    const data = formData.value as unknown as DataPermissionConfigVO
    if (formType.value === 'create') {
      await DataPermissionConfigApi.createDataPermissionConfig(data)
      message.success(t('common.createSuccess'))
    } else {
      await DataPermissionConfigApi.updateDataPermissionConfig(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    id: undefined,
    configKey: '',
    configName: '',
    businessType: '',
    roleIds: [],
    filterColumn: '',
    filterTable: '',
    status: 0, // 默认开启状态 (CommonStatusEnum.ENABLE = 0，从字典动态获取)
    remark: ''
  }
  formRef.value?.resetFields()
}
</script>
