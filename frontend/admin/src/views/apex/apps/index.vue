<template>
  <ContentWrap title="应用管理">
    <el-form class="-mb-15px" :model="queryParams" :inline="true" label-width="68px">
      <el-form-item label="关键词" prop="keywords">
        <el-input v-model="queryParams.keywords" placeholder="名称" class="!w-240px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" clearable placeholder="全部" class="!w-160px">
          <el-option label="启用" value="active" />
          <el-option label="停用" value="inactive" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery">
          <Icon class="mr-5px" icon="ep:search" /> 搜索
        </el-button>
        <el-button plain type="primary" @click="openCreate">
          <Icon class="mr-5px" icon="ep:plus" /> 新增
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true">
      <el-table-column align="center" label="编号" prop="id" width="90" />
      <el-table-column align="center" label="名称" prop="name" min-width="160" />
      <el-table-column align="center" label="描述" prop="description" min-width="220" />
      <el-table-column align="center" label="ApiKey" prop="apiKey" min-width="220" />
      <el-table-column align="center" label="状态" prop="status" width="110" />
      <el-table-column align="center" label="操作" width="220">
        <template #default="scope">
          <el-button link type="primary" @click="goDetail(scope.row)">详情</el-button>
          <el-button link type="primary" @click="openEdit(scope.row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(scope.row)">删除</el-button>
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

  <el-dialog v-model="createVisible" title="新增应用" width="640px">
    <el-form :model="createForm" label-width="90px">
      <el-form-item label="name">
        <el-input v-model="createForm.name" placeholder="例如：demo-app" />
      </el-form-item>
      <el-form-item label="description">
        <el-input v-model="createForm.description" placeholder="可选" />
      </el-form-item>
      <el-form-item label="callbackUrl">
        <el-input v-model="createForm.callbackUrl" placeholder="可选" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="createVisible = false">取消</el-button>
      <el-button type="primary" :loading="createLoading" @click="handleCreate">确定</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="editVisible" title="编辑应用" width="640px">
    <el-form :model="editForm" label-width="90px">
      <el-form-item label="name">
        <el-input v-model="editForm.name" placeholder="名称" />
      </el-form-item>
      <el-form-item label="description">
        <el-input v-model="editForm.description" placeholder="可选" />
      </el-form-item>
      <el-form-item label="callbackUrl">
        <el-input v-model="editForm.callbackUrl" placeholder="可选" />
      </el-form-item>
      <el-form-item label="status">
        <el-select v-model="editForm.status" clearable placeholder="可选">
          <el-option label="启用" value="active" />
          <el-option label="停用" value="inactive" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="editVisible = false">取消</el-button>
      <el-button type="primary" :loading="editLoading" @click="handleUpdate">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import ContentWrap from '@/components/ContentWrap/src/ContentWrap.vue'
import * as AppsApi from '@/api/apex/apps'

import { onMounted, reactive, ref } from 'vue'

defineOptions({ name: 'ApexApps' })

const message = useMessage()
const { push } = useRouter()

const loading = ref(false)
const list = ref<AppsApi.ApexDeveloperAppVO[]>([])
const total = ref(0)

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  keywords: undefined,
  status: undefined
})

const getList = async () => {
  loading.value = true
  try {
    const data = await AppsApi.getAppPage(queryParams)
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

const createVisible = ref(false)
const createLoading = ref(false)
const createForm = reactive<AppsApi.ApexDeveloperAppCreateReqVO>({
  name: '',
  description: '',
  callbackUrl: ''
})

const openCreate = () => {
  createVisible.value = true
  createForm.name = ''
  createForm.description = ''
  createForm.callbackUrl = ''
}

const handleCreate = async () => {
  createLoading.value = true
  try {
    const resp = await AppsApi.createApp(createForm)
    message.success('创建成功')
    createVisible.value = false
    message.alert(`ApiKey: ${resp.apiKey}\nApiSecret: ${resp.apiSecret}`, '请保存密钥')
    await getList()
  } finally {
    createLoading.value = false
  }
}

const editVisible = ref(false)
const editLoading = ref(false)
const editForm = reactive<any>({ id: undefined, name: '', description: '', callbackUrl: '', status: '' })

const openEdit = async (row: AppsApi.ApexDeveloperAppVO) => {
  const data = await AppsApi.getApp(row.id)
  editForm.id = data.id
  editForm.name = data.name
  editForm.description = data.description
  editForm.callbackUrl = data.callbackUrl
  editForm.status = data.status
  editVisible.value = true
}

const handleUpdate = async () => {
  editLoading.value = true
  try {
    await AppsApi.updateApp(editForm)
    message.success('保存成功')
    editVisible.value = false
    await getList()
  } finally {
    editLoading.value = false
  }
}

const handleDelete = async (row: AppsApi.ApexDeveloperAppVO) => {
  try {
    await message.delConfirm()
    await AppsApi.deleteApp(row.id)
    message.success('删除成功')
    await getList()
  } catch {}
}

const goDetail = (row: AppsApi.ApexDeveloperAppVO) => {
  push({ path: '/apex/app-detail/index', query: { id: row.id } })
}

onMounted(() => {
  getList()
})
</script>
