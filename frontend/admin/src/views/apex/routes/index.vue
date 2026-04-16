<template>
  <ContentWrap>
    <!-- 搜索栏 -->
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
      <el-form-item label="关键词" prop="keywords">
        <el-input
          v-model="queryParams.keywords"
          placeholder="请输入网关路径或上游地址"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 查询</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <el-table-column label="编号" align="center" prop="id" width="80" />
      <el-table-column label="API资产ID" align="center" prop="apiAssetId" width="100" />
      <el-table-column label="请求方法" align="center" prop="httpMethod" width="100">
        <template #default="scope">
          <el-tag :type="getMethodTag(scope.row.httpMethod)">{{ scope.row.httpMethod }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="网关开放路径" align="left" prop="gatewayPath" min-width="200" show-overflow-tooltip />
      <el-table-column label="上游转发路径" align="left" prop="upstreamPath" min-width="150" show-overflow-tooltip />
      <el-table-column label="最终物理地址" align="left" prop="upstreamUrl" min-width="250" show-overflow-tooltip>
        <template #default="scope">
          <el-link type="primary" :underline="false" :href="scope.row.upstreamUrl" target="_blank">
            {{ scope.row.upstreamUrl }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 'ACTIVE' ? 'success' : 'info'">
            {{ scope.row.status === 'ACTIVE' ? '生效中' : '已失效' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="120">
        <template #default="scope">
          <el-button link type="primary" @click="handleSync(scope.row)">
            同步权重
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '@/config/axios'

defineOptions({ name: 'ApexRouteRule' })

const loading = ref(true)
const total = ref(0)
const list = ref([])
const queryParams = ref({
  pageNo: 1,
  pageSize: 10,
  keywords: ''
})

const getList = async () => {
  loading.value = true
  try {
    const data = await request.get({
      url: '/apex/routes/page',
      params: queryParams.value
    })
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.value.pageNo = 1
  getList()
}

const resetQuery = () => {
  queryParams.value.keywords = ''
  handleQuery()
}

const getMethodTag = (method: string) => {
  const map = {
    GET: 'success',
    POST: 'warning',
    PUT: '',
    DELETE: 'danger'
  }
  return map[method] || 'info'
}

const handleSync = (row: any) => {
  // 占位功能
  console.log('Syncing route rule:', row.id)
}

onMounted(() => {
  getList()
})
</script>
