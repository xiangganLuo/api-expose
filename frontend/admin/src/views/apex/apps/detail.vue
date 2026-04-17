<template>
  <div v-loading="loading">
    <ContentWrap title="应用详情" class="mb-15px">
      <el-page-header @back="goBack" :content="appData.name" />
    </ContentWrap>

    <el-tabs v-model="activeTab" class="bg-white px-20px py-15px">
      <!-- Tab 1: 基础信息 -->
      <el-tab-pane label="基础信息" name="info">
        <el-descriptions border :column="2" class="mt-15px">
          <el-descriptions-item label="应用编号">
            {{ appData.id }}
          </el-descriptions-item>
          <el-descriptions-item label="应用名称">
            {{ appData.name }}
          </el-descriptions-item>
          <el-descriptions-item label="ApiKey（访问凭证）">
            <el-tag type="success">{{ appData.apiKey }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="appData.status === 'ACTIVE' ? 'success' : 'danger'">
              {{ appData.status === 'ACTIVE' ? '启用' : '禁用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="回调地址" :span="2">
            {{ appData.callbackUrl || '暂无配置' }}
          </el-descriptions-item>
          <el-descriptions-item label="应用描述" :span="2">
            {{ appData.description || '暂无描述' }}
          </el-descriptions-item>
        </el-descriptions>
      </el-tab-pane>

      <!-- Tab 2: API 资产分配 (订阅管理) -->
      <el-tab-pane label="API 资产分配" name="subscriptions">
        <div class="mb-15px flex justify-between">
          <div>
            <el-button type="primary" plain @click="openSubCreate">
              <Icon icon="ep:plus" class="mr-5px" /> 申请分配资产
            </el-button>
            <el-button @click="loadSubscriptions" :loading="subLoading">
              <Icon icon="ep:refresh" class="mr-5px" /> 刷新
            </el-button>
          </div>
        </div>
        <el-table v-loading="subLoading" :data="subList" :stripe="true">
          <el-table-column align="center" label="订阅编号" prop="id" width="100" />
          <el-table-column align="center" label="API资产ID" prop="apiAssetId" width="120" />
          <el-table-column align="center" label="资产名称/描述" min-width="200">
            <template #default="scope">
               <el-tag type="info" class="mr-5px">ID: {{scope.row.apiAssetId}}</el-tag>
               <!-- 这里如果没有关联回显名称，建议后端联表，当前直接显示 ID -->
            </template>
          </el-table-column>
          <el-table-column align="center" label="状态" prop="status" width="120">
            <template #default="scope">
              <el-tag :type="getSubStatusTag(scope.row.status)">
                {{ getSubStatusLabel(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column align="center" label="备注" prop="remark" min-width="150" />
          <el-table-column align="center" label="操作" width="160">
            <template #default="scope">
              <el-button 
                v-if="scope.row.status === 'pending'"
                link type="success" 
                @click="handleSubApprove(scope.row)">通过</el-button>
              <el-button 
                v-if="scope.row.status === 'pending'"
                link type="danger" 
                @click="handleSubReject(scope.row)">驳回</el-button>
            </template>
          </el-table-column>
        </el-table>
        <Pagination
          v-model:limit="subQueryParams.pageSize"
          v-model:page="subQueryParams.pageNo"
          :total="subTotal"
          @pagination="loadSubscriptions"
        />
      </el-tab-pane>
    </el-tabs>

    <!-- 分配资产对话框 -->
    <el-dialog v-model="subCreateVisible" title="申请分配资产" width="550px">
      <el-form :model="subCreateForm" label-width="100px">
        <el-form-item label="选择 API">
          <el-select 
            v-model="subCreateForm.apiAssetId" 
            filterable 
            remote
            reserve-keyword
            placeholder="请输入资产名称搜索..."
            remote-show-suffix
            :remote-method="handleAssetSearch"
            :loading="assetSearchLoading"
            class="w-full!"
          >
            <el-option
              v-for="item in assetOptions"
              :key="item.id"
              :label="item.name + ' (' + item.groupName + ')' "
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select v-model="subCreateForm.status" class="w-full!">
            <el-option label="待审核" value="pending" />
            <el-option label="直接通过" value="approved" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注说明">
          <el-input v-model="subCreateForm.remark" type="textarea" placeholder="填写业务或联系人说明..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="subCreateVisible = false">取消</el-button>
        <el-button type="primary" :loading="subSaveLoading" @click="handleSubCreate">确定分配</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import * as AppsApi from '@/api/apex/apps'
import * as SubscriptionsApi from '@/api/apex/subscriptions'
import * as AssetsApi from '@/api/apex/assets'

defineOptions({ name: 'ApexAppDetail' })

const { push } = useRouter()
const { query } = useRoute()
const message = useMessage()

const appId = parseInt(query.id as string) || 0
const loading = ref(false)
const activeTab = ref('info')

const appData = ref<any>({})

const goBack = () => {
  push({ path: '/apex/apps' })
}

const loadAppDetail = async () => {
  loading.value = true
  try {
    appData.value = await AppsApi.getApp(appId)
  } finally {
    loading.value = false
  }
}

// ==== 订阅管理模块 ==== //
const subLoading = ref(false)
const subList = ref<any[]>([])
const subTotal = ref(0)
const subQueryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  appId: appId
})

const loadSubscriptions = async () => {
  subLoading.value = true
  try {
    const data = await SubscriptionsApi.getSubscriptionPage(subQueryParams)
    subList.value = data.list
    subTotal.value = data.total
  } finally {
    subLoading.value = false
  }
}

const getSubStatusTag = (status: string) => {
  const map: Record<string, string> = { pending: 'warning', approved: 'success', rejected: 'danger', revoked: 'info' }
  return map[status] || 'info'
}

const getSubStatusLabel = (status: string) => {
  const map: Record<string, string> = { pending: '待审核', approved: '已通过', rejected: '已驳回', revoked: '已撤销' }
  return map[status] || '未知'
}

const handleSubApprove = async (row: any) => {
  await SubscriptionsApi.approveSubscription(row.id)
  message.success('已通过审批')
  await loadSubscriptions()
}

const handleSubReject = async (row: any) => {
  const { value } = await message.prompt('请输入驳回原因（可选）', '提示')
  await SubscriptionsApi.rejectSubscription(row.id, value)
  message.success('已驳回审批')
  await loadSubscriptions()
}

// 分配对话框
const subCreateVisible = ref(false)
const subSaveLoading = ref(false)
const subCreateForm = reactive({
  apiAssetId: undefined,
  status: 'approved',
  remark: ''
})

const assetSearchLoading = ref(false)
const assetOptions = ref<any[]>([])

// 搜索下拉框可绑定的资产
const handleAssetSearch = async (queryStr: string) => {
  assetSearchLoading.value = true
  try {
    const res = await AssetsApi.getApiAssetPage({ pageNo: 1, pageSize: 50, keywords: queryStr })
    assetOptions.value = res.list
  } catch (e) {
    console.error(e)
  } finally {
    assetSearchLoading.value = false
  }
}

const openSubCreate = async () => {
  subCreateForm.apiAssetId = undefined
  subCreateForm.status = 'approved'
  subCreateForm.remark = ''
  subCreateVisible.value = true
  // 首次打开加载默认下拉
  await handleAssetSearch('')
}

const handleSubCreate = async () => {
  if (!subCreateForm.apiAssetId) {
    message.warning('请选择需要分配的 API 资产')
    return
  }
  subSaveLoading.value = true
  try {
    await SubscriptionsApi.createSubscription({
      appId: appId,
      apiAssetId: subCreateForm.apiAssetId,
      status: subCreateForm.status,
      remark: subCreateForm.remark
    })
    message.success('分配成功')
    subCreateVisible.value = false
    await loadSubscriptions()
  } finally {
    subSaveLoading.value = false
  }
}

onMounted(async () => {
  if (!appId) {
    message.error('无效的应用ID')
    goBack()
    return
  }
  await loadAppDetail()
  await loadSubscriptions()
})
</script>
