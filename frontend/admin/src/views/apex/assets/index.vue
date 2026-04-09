<template>
  <ContentWrap title="API 资产">
    <el-form class="-mb-15px" :model="queryParams" :inline="true" label-width="68px">
      <el-form-item label="关键词" prop="keywords">
        <el-input v-model="queryParams.keywords" placeholder="名称/分组" class="!w-240px" />
      </el-form-item>
      <el-form-item label="分组" prop="groupName">
        <el-input v-model="queryParams.groupName" placeholder="例如：petstore" class="!w-240px" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" clearable placeholder="全部" class="!w-160px">
          <el-option label="草稿" value="draft" />
          <el-option label="已发布" value="published" />
          <el-option label="已废弃" value="deprecated" />
          <el-option label="已下线" value="offline" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">
          <Icon icon="ep:search" class="mr-5px" /> 查询
        </el-button>
        <el-button @click="openImport">
          <Icon icon="ep:upload" class="mr-5px" /> 导入
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" min-width="160" show-overflow-tooltip />
      <el-table-column prop="groupName" label="分组" min-width="140" show-overflow-tooltip />
      <el-table-column prop="protocolType" label="协议" width="90" />
      <el-table-column prop="status" label="状态" width="110" />
      <el-table-column prop="basePath" label="BasePath" min-width="160" show-overflow-tooltip />
      <el-table-column label="操作" width="260" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="openEdit(scope.row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(scope.row)">删除</el-button>
          <el-button link type="success" @click="handlePublish(scope.row)">发布</el-button>
          <el-button link type="warning" @click="handleOffline(scope.row)">下架</el-button>
          <el-button link type="danger" @click="handleDeprecate(scope.row)">废弃</el-button>
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

  <el-dialog v-model="importVisible" title="导入 API 资产" width="720px">
    <el-form :model="importForm" label-width="90px">
      <el-form-item label="groupName">
        <el-input v-model="importForm.groupName" placeholder="例如：petstore" />
      </el-form-item>
      <el-form-item label="fileContent">
        <el-input
          v-model="importForm.fileContent"
          type="textarea"
          :rows="10"
          placeholder="粘贴 OpenAPI JSON/YAML 内容"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="importVisible = false">取消</el-button>
      <el-button type="primary" :loading="importLoading" @click="handleImport">确定导入</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="editVisible" title="编辑资产" width="640px">
    <el-form :model="editForm" label-width="90px">
      <el-form-item label="name">
        <el-input v-model="editForm.name" placeholder="名称" />
      </el-form-item>
      <el-form-item label="groupName">
        <el-input v-model="editForm.groupName" placeholder="分组" />
      </el-form-item>
      <el-form-item label="basePath">
        <el-input v-model="editForm.basePath" placeholder="例如：/v1" />
      </el-form-item>
      <el-form-item label="status">
        <el-select v-model="editForm.status" clearable placeholder="可选">
          <el-option label="草稿" value="draft" />
          <el-option label="已发布" value="published" />
          <el-option label="已废弃" value="deprecated" />
          <el-option label="已下线" value="offline" />
        </el-select>
      </el-form-item>
      <el-form-item label="description">
        <el-input v-model="editForm.description" type="textarea" :rows="4" />
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
import * as AssetsApi from '@/api/apex/assets'

import { onMounted, reactive, ref } from 'vue'

defineOptions({ name: 'ApexAssets' })

const message = useMessage()

const loading = ref(false)
const list = ref<AssetsApi.ApexApiAssetVO[]>([])
const total = ref(0)

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  keywords: undefined,
  groupName: undefined,
  status: undefined
})

const getList = async () => {
  loading.value = true
  try {
    const data = await AssetsApi.getApiAssetPage(queryParams)
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

const importVisible = ref(false)
const importLoading = ref(false)
const importForm = reactive<AssetsApi.ApexApiImportReqVO>({
  groupName: '',
  fileContent: ''
})

const openImport = () => {
  importVisible.value = true
  importForm.groupName = ''
  importForm.fileContent = ''
}

const handleImport = async () => {
  importLoading.value = true
  try {
    await AssetsApi.importApiAsset(importForm)
    message.success('导入成功')
    importVisible.value = false
    await getList()
  } finally {
    importLoading.value = false
  }
}

const handlePublish = async (row: AssetsApi.ApexApiAssetVO) => {
  await AssetsApi.publishApiAsset(row.id)
  message.success('发布成功')
  await getList()
}

const handleOffline = async (row: AssetsApi.ApexApiAssetVO) => {
  await AssetsApi.offlineApiAsset(row.id)
  message.success('下架成功')
  await getList()
}

const handleDeprecate = async (row: AssetsApi.ApexApiAssetVO) => {
  await AssetsApi.deprecateApiAsset(row.id)
  message.success('废弃成功')
  await getList()
}

const editVisible = ref(false)
const editLoading = ref(false)
const editForm = reactive<any>({ id: undefined, name: '', groupName: '', basePath: '', status: '', description: '' })

const openEdit = async (row: AssetsApi.ApexApiAssetVO) => {
  const data = await AssetsApi.getApiAssetDetail(row.id)
  editForm.id = data.id
  editForm.name = data.name
  editForm.groupName = data.groupName
  editForm.basePath = data.basePath
  editForm.status = data.status
  editForm.description = (data as any).description
  editVisible.value = true
}

const handleUpdate = async () => {
  editLoading.value = true
  try {
    await AssetsApi.updateApiAsset(editForm)
    message.success('保存成功')
    editVisible.value = false
    await getList()
  } finally {
    editLoading.value = false
  }
}

const handleDelete = async (row: AssetsApi.ApexApiAssetVO) => {
  try {
    await message.delConfirm()
    await AssetsApi.deleteApiAsset(row.id)
    message.success('删除成功')
    await getList()
  } catch {}
}

onMounted(() => {
  getList()
})
</script>
