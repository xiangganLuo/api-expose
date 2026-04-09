<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="90px"
    >
      <el-form-item label="appId" prop="appId">
        <el-input-number v-model="queryParams.appId" :min="1" class="!w-220px" />
      </el-form-item>
      <el-form-item label="apiAssetId" prop="apiAssetId">
        <el-input-number v-model="queryParams.apiAssetId" :min="1" class="!w-220px" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" clearable placeholder="全部" class="!w-160px">
          <el-option label="待审核" value="pending" />
          <el-option label="已通过" value="approved" />
          <el-option label="已驳回" value="rejected" />
          <el-option label="已撤销" value="revoked" />
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
          <Icon class="mr-5px" icon="ep:plus" /> 新增订阅
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true">
      <el-table-column align="center" label="编号" prop="id" width="100" />
      <el-table-column align="center" label="appId" prop="appId" width="120" />
      <el-table-column align="center" label="apiAssetId" prop="apiAssetId" width="120" />
      <el-table-column align="center" label="状态" prop="status" width="120" />
      <el-table-column align="center" label="备注" prop="remark" min-width="200" />
      <el-table-column align="center" label="操作" width="220">
        <template #default="scope">
          <el-button link type="success" @click="handleApprove(scope.row)">通过</el-button>
          <el-button link type="danger" @click="handleReject(scope.row)">驳回</el-button>
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

  <el-dialog v-model="createVisible" title="新增订阅" width="640px">
    <el-form :model="createForm" label-width="90px">
      <el-form-item label="appId">
        <el-input-number v-model="createForm.appId" :min="1" class="!w-220px" />
      </el-form-item>
      <el-form-item label="apiAssetId">
        <el-input-number v-model="createForm.apiAssetId" :min="1" class="!w-220px" />
      </el-form-item>
      <el-form-item label="status">
        <el-select v-model="createForm.status" placeholder="默认 pending" class="!w-220px">
          <el-option label="待审核" value="pending" />
          <el-option label="直接通过" value="approved" />
        </el-select>
      </el-form-item>
      <el-form-item label="remark">
        <el-input v-model="createForm.remark" placeholder="可选" class="!w-360px" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="createVisible = false">取消</el-button>
      <el-button type="primary" :loading="createLoading" @click="handleCreate">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import ContentWrap from '@/components/ContentWrap/src/ContentWrap.vue'
import * as SubscriptionsApi from '@/api/apex/subscriptions'

import { onMounted, reactive, ref } from 'vue'

defineOptions({ name: 'ApexSubscriptions' })

const message = useMessage()

const loading = ref(true)
const total = ref(0)
const list = ref<SubscriptionsApi.ApexSubscriptionVO[]>([])

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  appId: undefined,
  apiAssetId: undefined,
  status: undefined
})
const queryFormRef = ref()

const getList = async () => {
  loading.value = true
  try {
    const data = await SubscriptionsApi.getSubscriptionPage(queryParams)
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

const createVisible = ref(false)
const createLoading = ref(false)
const createForm = reactive<SubscriptionsApi.ApexSubscriptionCreateReqVO>({
  appId: 0,
  apiAssetId: 0,
  remark: '',
  status: 'pending'
})

const openCreate = () => {
  createVisible.value = true
  createForm.appId = 0
  createForm.apiAssetId = 0
  createForm.remark = ''
  createForm.status = 'pending'
}

const handleCreate = async () => {
  createLoading.value = true
  try {
    const id = await SubscriptionsApi.createSubscription(createForm)
    message.success(`创建成功，ID=${id}`)
    createVisible.value = false
    await getList()
  } finally {
    createLoading.value = false
  }
}

const handleApprove = async (row: SubscriptionsApi.ApexSubscriptionVO) => {
  await SubscriptionsApi.approveSubscription(row.id)
  message.success('已通过')
  await getList()
}

const handleReject = async (row: SubscriptionsApi.ApexSubscriptionVO) => {
  const { value } = await message.prompt('请输入驳回原因（可选）', '提示')
  await SubscriptionsApi.rejectSubscription(row.id, value)
  message.success('已驳回')
  await getList()
}

onMounted(() => {
  getList()
})
</script>
