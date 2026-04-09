<template>
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="90px"
    >
      <el-form-item label="scope" prop="scope">
        <el-select v-model="queryParams.scope" clearable class="!w-200px" placeholder="全部">
          <el-option label="GLOBAL" value="GLOBAL" />
          <el-option label="API_LEVEL" value="API_LEVEL" />
          <el-option label="APP_LEVEL" value="APP_LEVEL" />
        </el-select>
      </el-form-item>
      <el-form-item label="apiAssetId" prop="apiAssetId">
        <el-input-number v-model="queryParams.apiAssetId" class="!w-200px" :min="1" />
      </el-form-item>
      <el-form-item label="appId" prop="appId">
        <el-input-number v-model="queryParams.appId" class="!w-200px" :min="1" />
      </el-form-item>
      <el-form-item label="enabled" prop="enabled">
        <el-select v-model="queryParams.enabled" clearable class="!w-200px" placeholder="全部">
          <el-option label="启用" :value="true" />
          <el-option label="禁用" :value="false" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery">
          <Icon class="mr-5px" icon="ep:search" /> 搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon class="mr-5px" icon="ep:refresh" /> 重置
        </el-button>
        <el-button plain type="primary" @click="openCreate">
          <Icon class="mr-5px" icon="ep:plus" /> 新增
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true">
      <el-table-column align="center" label="编号" prop="id" width="100" />
      <el-table-column align="center" label="名称" prop="policyName" min-width="160" />
      <el-table-column align="center" label="范围" prop="scope" width="120" />
      <el-table-column align="center" label="apiAssetId" prop="apiAssetId" width="120" />
      <el-table-column align="center" label="appId" prop="appId" width="120" />
      <el-table-column align="center" label="启用" prop="enabled" width="90">
        <template #default="scope">
          <el-switch v-model="scope.row.enabled" @change="(val) => handleUpdateStatus(scope.row.id, val as boolean)" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="操作" width="220">
        <template #default="scope">
          <el-button link type="primary" @click="openEdit(scope.row.id)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination
      v-model:limit="queryParams.pageSize"
      v-model:page="queryParams.pageNo"
      :total="total"
      @pagination="getList"
    />
  </ContentWrap>

  <el-dialog v-model="formVisible" :title="formType === 'create' ? '新增策略' : '编辑策略'" width="720px">
    <el-form :model="form" label-width="120px">
      <el-form-item label="policyName">
        <el-input v-model="form.policyName" placeholder="策略名称" />
      </el-form-item>
      <el-form-item label="scope">
        <el-select v-model="form.scope" class="!w-240px">
          <el-option label="GLOBAL" value="GLOBAL" />
          <el-option label="API_LEVEL" value="API_LEVEL" />
          <el-option label="APP_LEVEL" value="APP_LEVEL" />
        </el-select>
      </el-form-item>
      <el-form-item label="apiAssetId">
        <el-input-number v-model="form.apiAssetId" class="!w-240px" :min="1" />
      </el-form-item>
      <el-form-item label="appId">
        <el-input-number v-model="form.appId" class="!w-240px" :min="1" />
      </el-form-item>
      <el-form-item label="enabled">
        <el-switch v-model="form.enabled" />
      </el-form-item>
      <el-form-item label="rateLimitJson">
        <el-input v-model="form.rateLimitJson" type="textarea" :rows="3" placeholder="可选" />
      </el-form-item>
      <el-form-item label="circuitBreakerJson">
        <el-input v-model="form.circuitBreakerJson" type="textarea" :rows="3" placeholder="可选" />
      </el-form-item>
      <el-form-item label="accessControlJson">
        <el-input v-model="form.accessControlJson" type="textarea" :rows="3" placeholder="可选" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="formVisible = false">取消</el-button>
      <el-button type="primary" :loading="formLoading" @click="handleSubmit">保存</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import ContentWrap from '@/components/ContentWrap/src/ContentWrap.vue'
import * as PolicyApi from '@/api/apex/policies'

import { onMounted, reactive, ref } from 'vue'

defineOptions({ name: 'ApexPolicies' })

const message = useMessage()
const loading = ref(true)
const total = ref(0)
const list = ref<PolicyApi.ApexPolicyVO[]>([])

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  scope: undefined,
  apiAssetId: undefined,
  appId: undefined,
  enabled: undefined
})
const queryFormRef = ref()

const getList = async () => {
  loading.value = true
  try {
    const data = await PolicyApi.getPolicyPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

const resetQuery = () => {
  queryFormRef.value?.resetFields()
  handleQuery()
}

const formVisible = ref(false)
const formLoading = ref(false)
const formType = ref<'create' | 'update'>('create')
const form = reactive<any>({
  id: undefined,
  policyName: '',
  scope: 'GLOBAL',
  apiAssetId: undefined,
  appId: undefined,
  enabled: true,
  rateLimitJson: '',
  circuitBreakerJson: '',
  accessControlJson: ''
})

const openCreate = () => {
  formType.value = 'create'
  formVisible.value = true
  form.id = undefined
  form.policyName = ''
  form.scope = 'GLOBAL'
  form.apiAssetId = undefined
  form.appId = undefined
  form.enabled = true
  form.rateLimitJson = ''
  form.circuitBreakerJson = ''
  form.accessControlJson = ''
}

const openEdit = async (id: number) => {
  formType.value = 'update'
  const data = await PolicyApi.getPolicy(id)
  formVisible.value = true
  Object.assign(form, data)
}

const handleSubmit = async () => {
  formLoading.value = true
  try {
    if (formType.value === 'create') {
      await PolicyApi.createPolicy(form)
      message.success('创建成功')
    } else {
      await PolicyApi.updatePolicy(form)
      message.success('保存成功')
    }
    formVisible.value = false
    await getList()
  } finally {
    formLoading.value = false
  }
}

const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await PolicyApi.deletePolicy(id)
    message.success('删除成功')
    await getList()
  } catch {}
}

const handleUpdateStatus = async (id: number, enabled: boolean) => {
  await PolicyApi.updatePolicyStatus(id, enabled)
  message.success('操作成功')
}

onMounted(() => {
  getList()
})
</script>
