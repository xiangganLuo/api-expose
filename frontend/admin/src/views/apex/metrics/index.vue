<template>
  <ContentWrap title="监控指标">
    <el-form class="-mb-15px" :inline="true" :model="queryParams" label-width="100px">
      <el-form-item label="查询维度" prop="dimension">
        <el-select class="!w-100px"  v-model="queryParams.dimension" placeholder="选择维度" @change="handleDimensionChange">
          <el-option label="全局" value="global" />
          <el-option label="按应用" value="app" />
          <el-option label="按资产" value="asset" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="queryParams.dimension === 'app'" label="应用" prop="appId">
        <el-select
          v-model="queryParams.appId"
          filterable
          remote
          reserve-keyword
          placeholder="输入应用名称搜索"
          :remote-method="searchApps"
          :loading="appLoading"
          clearable
          class="!w-240px" 
        >
          <el-option v-for="app in appOptions" :key="app.appId" :label="app.appName" :value="app.appId" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="queryParams.dimension === 'asset'" label="API资产" prop="apiAssetId">
        <el-select
          v-model="queryParams.apiAssetId"
          filterable
          remote
          reserve-keyword
          placeholder="输入资产名称搜索"
          :remote-method="searchAssets"
          :loading="assetLoading"
          clearable
          class="!w-240px" 
        >
          <el-option v-for="asset in assetOptions" :key="asset.id" :label="asset.assetName" :value="asset.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="近N天" prop="days">
        <el-input-number v-model="queryParams.days" :min="1" :max="30" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">
          <Icon icon="ep:search" class="mr-5px" /> 查询
        </el-button>
        <el-button @click="resetQuery"> <Icon icon="ep:refresh" class="mr-5px" /> 重置 </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <div v-loading="loading">
    <el-row :gutter="12">
      <el-col :span="24">
        <el-card shadow="never">
          <Echart :height="420" :options="trendOption" />
        </el-card>
      </el-col>
    </el-row>
    </div>
  </ContentWrap>
</template>

<script setup lang="ts">
import ContentWrap from '@/components/ContentWrap/src/ContentWrap.vue'
import * as MetricsApi from '@/api/apex/metrics'
import { reactive, ref } from 'vue'

defineOptions({ name: 'ApexMetrics' })

const message = useMessage()

const queryParams = reactive({
  dimension: 'global',
  days: 7
})

const trendOption = ref<any>({
  title: {
    text: '调用趋势',
    left: 'center'
  },
  tooltip: {
    trigger: 'axis'
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: []
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      name: '调用次数',
      type: 'line',
      smooth: true,
      data: []
    }
  ]
})

const loading = ref(false)

const loadTrend = async () => {
  loading.value = true
  try {
    const res = await MetricsApi.getTrafficTrend(queryParams.days)
    const keys = Object.keys(res || {}).sort()
    const values = keys.map((k) => res[k] ?? 0)
    trendOption.value.xAxis.data = keys
    trendOption.value.series[0].data = values
  } catch (e) {
    console.error(e)
    message.error('加载监控指标失败')
  } finally {
    loading.value = false
  }
}

const handleQuery = async () => {
  await loadTrend()
}

const resetQuery = async () => {
  queryParams.days = 7
  await loadTrend()
}

loadTrend()
</script>
