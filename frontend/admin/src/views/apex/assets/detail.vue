<template>
  <el-page-header @back="push('/apex/assets')">
    <template #content>
      <span class="text-large font-600 mr-3">资产详情: {{ assetDetail.name }}</span>
      <el-tag :type="statusTagType">{{ assetDetail.status }}</el-tag>
    </template>
    <template #extra>
      <div class="flex items-center">
        <el-select v-model="targetEnvCode" placeholder="选择发布环境" class="!w-150px mr-5px">
          <el-option v-for="env in assetEnvs" :key="env.id" :label="env.envName" :value="env.envCode" />
          <template #empty>
            <div class="p-2 text-center text-gray-400 text-xs">
              无可用环境，请先在环境配置中添加
            </div>
          </template>
        </el-select>
        <el-button type="primary" :loading="updateLoading" @click="handleUpdateAsset">保存基本信息</el-button>
        <el-button type="success" @click="handlePublish">发布资产</el-button>
      </div>
    </template>
  </el-page-header>

  <el-row :gutter="20" class="mt-20px">
    <el-col :span="24">
      <el-card shadow="never">
        <el-form :model="assetDetail" label-width="100px" :inline="true">
          <el-form-item label="资产名称">
            <el-input v-model="assetDetail.name" class="!w-220px" />
          </el-form-item>
          <el-form-item label="分组名称">
            <el-input v-model="assetDetail.groupName" class="!w-220px" />
          </el-form-item>
          <el-form-item label="基础路径">
            <el-input v-model="assetDetail.basePath" placeholder="/v1" class="!w-220px" />
          </el-form-item>
          <el-form-item label="协议类型">
            <el-select v-model="assetDetail.protocolType" class="!w-120px">
              <el-option label="HTTP" value="HTTP" />
              <el-option label="HTTPS" value="HTTPS" />
            </el-select>
          </el-form-item>
          <el-form-item label="描述" class="!w-full">
            <el-input v-model="assetDetail.description" type="textarea" :rows="2" class="!w-600px" />
          </el-form-item>
        </el-form>
      </el-card>
    </el-col>
  </el-row>

  <el-tabs v-model="activeTab" class="mt-20px">
    <!-- 端点管理 -->
    <el-tab-pane label="端点管理" name="endpoints">
      <div class="mb-10px flex justify-between">
        <span class="text-gray-500">管理该资产下的具体 API 路径及配置</span>
        <el-button type="primary" @click="openEndpointAdd">
          <Icon icon="ep:plus" class="mr-5px" /> 新增端点
        </el-button>
      </div>
      <el-table :data="endpoints" v-loading="endpointsLoading" border>
        <el-table-column prop="path" label="路径" min-width="200" show-overflow-tooltip />
        <el-table-column prop="httpMethod" label="方法" width="100">
          <template #default="scope">
            <el-tag :type="methodTagType(scope.row.httpMethod)">{{ scope.row.httpMethod }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="名称" min-width="150" />
        <el-table-column prop="upstreamUrl" label="相对路径/直接路径" min-width="200" show-overflow-tooltip />
        <el-table-column prop="timeoutMs" label="超时(ms)" width="100" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="openEndpointEdit(scope.row)">编辑</el-button>
            <el-button link type="danger" @click="handleEndpointDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-tab-pane>

    <!-- 环境配置 -->
    <el-tab-pane label="环境配置" name="envs">
      <div class="mb-10px flex justify-between">
        <span class="text-gray-500">为该资产配置不同环境下的后端 Base URL</span>
        <el-button type="primary" link @click="openEnvAdd">
          <Icon icon="ep:plus" class="mr-5px" /> 添加环境
        </el-button>
      </div>
      <el-table :data="assetEnvs" border>
        <el-table-column prop="envName" label="环境名称" width="150" />
        <el-table-column prop="envCode" label="环境代码" width="120" />
        <el-table-column prop="baseUrl" label="后端 Base URL" min-width="300" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 0 ? 'success' : 'danger'">
              {{ scope.row.status === 0 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="openEnvEdit(scope.row)">编辑</el-button>
            <el-button link type="danger" @click="handleEnvDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-tab-pane>

    <!-- 版本记录 -->
    <el-tab-pane label="版本记录" name="versions">
      <el-table :data="versions" v-loading="versionsLoading" border>
        <el-table-column prop="version" label="版本号" width="120" />
        <el-table-column prop="active" label="当前生效" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.active === 1 ? 'success' : 'info'">
              {{ scope.row.active === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="releaseNote" label="发布说明" min-width="300" />
        <el-table-column prop="createTime" label="发布时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
      </el-table>
    </el-tab-pane>

    <!-- 在线调试 -->
    <el-tab-pane label="在线调试" name="debug">
      <el-alert title="调试说明" type="info" show-icon :closable="false" class="mb-15px">
        选择一个已配置的环境，系统将根据该环境的 Base URL 自动路由。
      </el-alert>
      <el-form :model="tryForm" label-width="120px" class="max-w-800px">
        <el-form-item label="调试环境">
          <el-select v-model="tryForm.envCode" placeholder="选择调试环境" class="!w-full">
            <el-option v-for="env in assetEnvs" :key="env.id" :label="env.envName" :value="env.envCode" />
          </el-select>
        </el-form-item>
        <el-form-item label="端点路径">
          <el-select v-model="tryForm.endpointPath" placeholder="选择或输入路径" filterable allow-create @change="onTryPathChange" class="!w-full">
            <el-option v-for="e in endpoints" :key="e.id" :label="e.path" :value="e.path" />
          </el-select>
        </el-form-item>
        <el-form-item label="HTTP 方法">
          <el-radio-group v-model="tryForm.httpMethod">
            <el-radio label="GET">GET</el-radio>
            <el-radio label="POST">POST</el-radio>
            <el-radio label="PUT">PUT</el-radio>
            <el-radio label="DELETE">DELETE</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="请求头 (JSON)">
          <el-input v-model="tryForm.headersJson" type="textarea" :rows="3" placeholder='{"Content-Type": "application/json"}' />
        </el-form-item>
        <el-form-item label="请求体">
          <el-input v-model="tryForm.body" type="textarea" :rows="6" placeholder="请求 Body..." />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="tryLoading" @click="handleTry">发送请求</el-button>
        </el-form-item>
      </el-form>
      <div v-if="tryResult" class="mt-20px">
        <p class="font-bold mb-5px">响应结果：</p>
        <el-input type="textarea" :rows="10" :model-value="tryResult" readonly />
      </div>
    </el-tab-pane>
  </el-tabs>

  <!-- 端点编辑弹窗 -->
  <el-dialog v-model="endpointDialog.visible" :title="endpointDialog.isEdit ? '编辑端点' : '新增端点'" width="700px">
    <el-form :model="endpointForm" label-width="100px" :rules="endpointRules" ref="endpointFormRef">
      <el-form-item label="名称" prop="name">
        <el-input v-model="endpointForm.name" />
      </el-form-item>
      <el-form-item label="路径" prop="path">
        <el-input v-model="endpointForm.path" placeholder="/api/v1/..." />
      </el-form-item>
      <el-form-item label="HTTP 方法" prop="httpMethod">
        <el-select v-model="endpointForm.httpMethod" class="!w-full">
          <el-option label="GET" value="GET" />
          <el-option label="POST" value="POST" />
          <el-option label="PUT" value="PUT" />
          <el-option label="DELETE" value="DELETE" />
        </el-select>
      </el-form-item>
      <el-form-item label="上游地址" prop="upstreamUrl">
        <el-input v-model="endpointForm.upstreamUrl" placeholder="相对路径 (如 /users) 或 全路径" />
      </el-form-item>
      <el-form-item label="超时(ms)" prop="timeoutMs">
        <el-input-number v-model="endpointForm.timeoutMs" :min="0" :step="500" />
      </el-form-item>
      <el-form-item label="精简描述">
        <el-input v-model="endpointForm.summary" type="textarea" :rows="2" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="endpointDialog.visible = false">取消</el-button>
      <el-button type="primary" :loading="endpointDialog.loading" @click="handleEndpointSave">保存</el-button>
    </template>
  </el-dialog>

  <!-- 环境配置弹窗 -->
  <el-dialog v-model="envDialog.visible" :title="envDialog.isEdit ? '编辑环境' : '添加环境'" width="500px">
    <el-form :model="envForm" label-width="100px" :rules="envRules" ref="envFormRef">
      <el-form-item label="环境名称" prop="envName">
        <el-input v-model="envForm.envName" placeholder="如：测试环境" />
      </el-form-item>
      <el-form-item label="环境代码" prop="envCode">
        <el-input v-model="envForm.envCode" placeholder="如：test" :disabled="envDialog.isEdit" />
      </el-form-item>
      <el-form-item label="Base URL" prop="baseUrl">
        <el-input v-model="envForm.baseUrl" placeholder="如：http://api.test.com" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="envForm.status">
          <el-radio :label="0">启用</el-radio>
          <el-radio :label="1">禁用</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="envDialog.visible = false">取消</el-button>
      <el-button type="primary" :loading="envDialog.loading" @click="handleEnvSave">确认</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import * as AssetsApi from '@/api/apex/assets'
import { formatDate } from '@/utils/formatTime'

defineOptions({ name: 'ApexAssetDetail' })

const route = useRoute()
const { push } = useRouter()
const message = useMessage()
const assetId = Number(route.query.id)

const activeTab = ref('endpoints')
const targetEnvCode = ref('test')

// ====== 资产详情 ======
const assetDetail = ref<any>({
  id: undefined,
  name: '',
  groupName: '',
  basePath: '',
  protocolType: 'HTTP',
  status: '',
  description: ''
})
const updateLoading = ref(false)

const loadAssetDetail = async () => {
  if (!assetId) return
  const data = await AssetsApi.getApiAssetDetail(assetId)
  assetDetail.value = data
}

const handleUpdateAsset = async () => {
  updateLoading.value = true
  try {
    await AssetsApi.updateApiAsset(assetDetail.value)
    message.success('资产基本信息保存成功')
    await loadAssetDetail()
  } finally {
    updateLoading.value = false
  }
}

const statusTagType = computed(() => {
  const s = assetDetail.value.status
  if (s === 'published') return 'success'
  if (s === 'draft') return 'info'
  if (s === 'offline') return 'warning'
  return 'danger'
})

const handlePublish = async () => {
  if (!targetEnvCode.value) {
    message.error('请选择发布环境')
    return
  }
  await AssetsApi.publishApiAsset(assetId, targetEnvCode.value)
  message.success('发布成功')
  await loadAssetDetail()
  await loadVersions()
}

// ====== 端点管理 ======
const endpoints = ref<any[]>([])
const endpointsLoading = ref(false)
const loadEndpoints = async () => {
  endpointsLoading.value = true
  try {
    endpoints.value = await AssetsApi.getEndpoints(assetId)
  } finally {
    endpointsLoading.value = false
  }
}

const endpointDialog = reactive({
  visible: false,
  loading: false,
  isEdit: false
})

const endpointForm = ref<any>({
  id: undefined,
  assetId: assetId,
  path: '',
  httpMethod: 'GET',
  name: '',
  summary: '',
  upstreamUrl: '',
  timeoutMs: 3000,
  requestSchema: '',
  responseSchema: ''
})

const endpointRules = {
  name: [{ required: true, message: '请输入端点名称', trigger: 'blur' }],
  path: [{ required: true, message: '请输入端点路径', trigger: 'blur' }],
  httpMethod: [{ required: true, message: '请选择请求方法', trigger: 'change' }],
  upstreamUrl: [{ required: true, message: '请输入上游地址', trigger: 'blur' }]
}

const openEndpointAdd = () => {
  endpointForm.value = {
    id: undefined,
    assetId: assetId,
    path: '',
    httpMethod: 'GET',
    name: '',
    summary: '',
    upstreamUrl: '',
    timeoutMs: 3000,
    requestSchema: '',
    responseSchema: ''
  }
  endpointDialog.isEdit = false
  endpointDialog.visible = true
}

const openEndpointEdit = (row: any) => {
  endpointForm.value = { ...row }
  endpointDialog.isEdit = true
  endpointDialog.visible = true
}

const handleEndpointSave = async () => {
  endpointDialog.loading = true
  try {
    await AssetsApi.saveEndpoint(endpointForm.value)
    message.success('端点保存成功')
    endpointDialog.visible = false
    await loadEndpoints()
  } finally {
    endpointDialog.loading = false
  }
}

const handleEndpointDelete = async (id: number) => {
  await message.delConfirm()
  await AssetsApi.deleteEndpoint(id)
  message.success('删除成功')
  await loadEndpoints()
}

const methodTagType = (method: string) => {
  if (method === 'GET') return 'success'
  if (method === 'POST') return ''
  if (method === 'PUT') return 'warning'
  return 'danger'
}

// ====== 环境配置 ======
const assetEnvs = ref<any[]>([])
const loadAssetEnvs = async () => {
  assetEnvs.value = await AssetsApi.getAssetEnvs(assetId)
  if (assetEnvs.value.length > 0) {
    const hasTest = assetEnvs.value.some(e => e.envCode === 'test')
    targetEnvCode.value = hasTest ? 'test' : assetEnvs.value[0].envCode
    tryForm.envCode = targetEnvCode.value
  }
}

const envDialog = reactive({
  visible: false,
  loading: false,
  isEdit: false
})
const envForm = ref<any>({
  id: undefined,
  assetId: assetId,
  envCode: '',
  envName: '',
  baseUrl: '',
  status: 0
})
const envRules = {
  envCode: [{ required: true, message: '请输入环境代码', trigger: 'blur' }],
  envName: [{ required: true, message: '请输入环境名称', trigger: 'blur' }],
  baseUrl: [{ required: true, message: '请输入 Base URL', trigger: 'blur' }]
}

const openEnvAdd = () => {
  envForm.value = { id: undefined, assetId, envCode: '', envName: '', baseUrl: '', status: 0 }
  envDialog.isEdit = false
  envDialog.visible = true
}
const openEnvEdit = (row: any) => {
  envForm.value = { ...row }
  envDialog.isEdit = true
  envDialog.visible = true
}
const handleEnvSave = async () => {
  envDialog.loading = true
  try {
    await AssetsApi.saveAssetEnv(envForm.value)
    message.success('保存成功')
    envDialog.visible = false
    await loadAssetEnvs()
  } finally {
    envDialog.loading = false
  }
}
const handleEnvDelete = async (id: number) => {
  await message.delConfirm()
  await AssetsApi.deleteAssetEnv(id)
  message.success('删除成功')
  await loadAssetEnvs()
}

// ====== 版本记录 ======
const versions = ref<any[]>([])
const versionsLoading = ref(false)
const loadVersions = async () => {
  versionsLoading.value = true
  try {
    versions.value = await AssetsApi.getVersions(assetId)
  } finally {
    versionsLoading.value = false
  }
}

// ====== 在线调试 ======
const tryLoading = ref(false)
const tryResult = ref('')
const tryForm = reactive({
  envCode: 'test',
  endpointPath: '',
  httpMethod: 'GET',
  headersJson: '',
  body: ''
})

const onTryPathChange = (val: string) => {
  const match = endpoints.value.find(e => e.path === val)
  if (match) {
    tryForm.httpMethod = match.httpMethod
  }
}

const handleTry = async () => {
  tryLoading.value = true
  try {
    let headers = {}
    if (tryForm.headersJson) {
      try {
        headers = JSON.parse(tryForm.headersJson)
      } catch {
        message.error('请求头格式错误')
        return
      }
    }
    const resp = await AssetsApi.tryApiAsset({
      assetId,
      envCode: tryForm.envCode,
      endpointPath: tryForm.endpointPath,
      httpMethod: tryForm.httpMethod,
      headers,
      body: tryForm.body
    })
    if (resp instanceof ArrayBuffer) {
      tryResult.value = new TextDecoder().decode(resp)
    } else {
      tryResult.value = typeof resp === 'string' ? resp : JSON.stringify(resp, null, 2)
    }
  } catch (e: any) {
    tryResult.value = `Error: ${e.message}`
  } finally {
    tryLoading.value = false
  }
}

onMounted(async () => {
  await loadAssetEnvs()
  await loadAssetDetail()
  await loadEndpoints()
  await loadVersions()
})
</script>

<style scoped>
.mt-20px { margin-top: 20px; }
.mb-10px { margin-bottom: 10px; }
.text-large { font-size: 1.25rem; }
.font-600 { font-weight: 600; }
.max-w-800px { max-width: 800px; }
</style>
