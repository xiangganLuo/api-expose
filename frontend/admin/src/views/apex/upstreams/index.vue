<template>
  <ContentWrap>
    <el-tabs v-model="activeTab">
      <!-- 运行环境管理 -->
      <el-tab-pane label="运行环境" name="environments">
        <div class="mb-4">
          <el-button type="primary" @click="handleOpenEnvDialog()">
            <Icon icon="ep:plus" class="mr-1" /> 新增环境
          </el-button>
        </div>
        <el-table :data="envList" v-loading="loading">
          <el-table-column label="环境标识" prop="code" width="150" />
          <el-table-column label="环境名称" prop="name" width="200" />
          <el-table-column label="排序" prop="sort" width="100" />
          <el-table-column label="状态" prop="status" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === 0 ? 'success' : 'danger'">
                {{ scope.row.status === 0 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" fixed="right" width="150">
            <template #default="scope">
              <el-button type="primary" link @click="handleOpenEnvDialog(scope.row)"> 编辑 </el-button>
              <el-button type="danger" link @click="handleDeleteEnv(scope.row)"> 删除 </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 上游服务管理 -->
      <el-tab-pane label="上游服务" name="upstreams">
        <div class="mb-4">
          <el-button type="primary" @click="handleOpenUpstreamDialog()">
            <Icon icon="ep:plus" class="mr-1" /> 新增上游
          </el-button>
        </div>
        <el-table :data="upstreamList" v-loading="loading">
          <el-table-column label="服务名称" prop="name" width="200" />
          <el-table-column label="描述" prop="description" show-overflow-tooltip />
          <el-table-column label="操作" fixed="right" width="150">
            <template #default="scope">
              <el-button type="primary" link @click="handleOpenUpstreamDialog(scope.row)"> 编辑 </el-button>
              <el-button type="danger" link @click="handleDeleteUpstream(scope.row)"> 删除 </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 环境表单弹窗 -->
    <el-dialog v-model="envDialog.visible" :title="envDialog.title" width="500px">
      <el-form :model="envDialog.form" label-width="80px">
        <el-form-item label="环境标识">
          <el-input v-model="envDialog.form.code" placeholder="如: test, prod" />
        </el-form-item>
        <el-form-item label="环境名称">
          <el-input v-model="envDialog.form.name" placeholder="如: 测试环境" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="envDialog.form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="envDialog.form.status">
            <el-radio :label="0">启用</el-radio>
            <el-radio :label="1">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="envDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitEnv">保存</el-button>
      </template>
    </el-dialog>

    <!-- 上游表单弹窗 -->
    <el-dialog v-model="upstreamDialog.visible" :title="upstreamDialog.title" width="700px">
      <el-form :model="upstreamDialog.form" label-width="100px">
        <el-form-item label="服务名称">
          <el-input v-model="upstreamDialog.form.name" placeholder="输入核心服务名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="upstreamDialog.form.description" type="textarea" />
        </el-form-item>
        <el-divider content-position="left">各环境地址配置</el-divider>
        <div v-for="env in envList" :key="env.id" class="mb-2">
          <el-form-item :label="env.name">
            <el-input
              v-model="getUpstreamConfig(env.id).baseUrl"
              placeholder="http://base-url:port"
            >
              <template #prepend>{{ env.code }}</template>
            </el-input>
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="upstreamDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitUpstream">保存</el-button>
      </template>
    </el-dialog>
  </ContentWrap>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import {
  getEnvironmentList,
  saveEnvironment,
  deleteEnvironment,
  getUpstreamList,
  getUpstream,
  saveUpstream,
  deleteUpstream
} from '@/api/apex/upstreams'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const activeTab = ref('environments')
const envList = ref([])
const upstreamList = ref([])

// 获取列表
const getListData = async () => {
  loading.value = true
  try {
    const envs = await getEnvironmentList()
    envList.value = envs
    const ups = await getUpstreamList()
    upstreamList.value = ups
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  getListData()
})

// 环境弹窗逻辑
const envDialog = reactive({
  visible: false,
  title: '新增环境',
  form: { id: null, code: '', name: '', sort: 0, status: 0 }
})
const handleOpenEnvDialog = (row?: any) => {
  envDialog.visible = true
  if (row) {
    envDialog.title = '编辑环境'
    Object.assign(envDialog.form, row)
  } else {
    envDialog.title = '新增环境'
    envDialog.form = { id: null, code: '', name: '', sort: 0, status: 0 }
  }
}
const handleSubmitEnv = async () => {
  await saveEnvironment(envDialog.form)
  ElMessage.success('操作成功')
  envDialog.visible = false
  getListData()
}
const handleDeleteEnv = async (row: any) => {
  await ElMessageBox.confirm('确定删除该环境吗？')
  await deleteEnvironment(row.id)
  ElMessage.success('删除成功')
  getListData()
}

// 上游管理逻辑
const upstreamDialog = reactive({
  visible: false,
  title: '新增上游',
  form: { id: null, name: '', description: '', configs: [] as any[] }
})
const handleOpenUpstreamDialog = async (row?: any) => {
  if (row) {
    const detail = await getUpstream(row.id)
    upstreamDialog.title = '编辑上游'
    upstreamDialog.form = detail
  } else {
    upstreamDialog.title = '新增上游'
    upstreamDialog.form = { id: null, name: '', description: '', configs: [] }
  }
  upstreamDialog.visible = true
}
const getUpstreamConfig = (envId: number) => {
  let config = upstreamDialog.form.configs.find((c: any) => c.envId === envId)
  if (!config) {
    config = { envId, baseUrl: '' }
    upstreamDialog.form.configs.push(config)
  }
  return config
}
const handleSubmitUpstream = async () => {
  // 过滤空地址
  upstreamDialog.form.configs = upstreamDialog.form.configs.filter(c => c.baseUrl)
  await saveUpstream(upstreamDialog.form)
  ElMessage.success('操作成功')
  upstreamDialog.visible = false
  getListData()
}
const handleDeleteUpstream = async (row: any) => {
  await ElMessageBox.confirm('确定删除该上游吗？')
  await deleteUpstream(row.id)
  ElMessage.success('删除成功')
  getListData()
}
</script>
