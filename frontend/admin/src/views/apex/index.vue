<template>
  <ContentWrap title="APEX">
    <el-row :gutter="12">
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>资产</template>
          <div class="text-gray-500 mb-10px">导入 / 发布 / 下架 / 文档</div>
          <el-button type="primary" @click="go('/apex/assets')">进入资产管理</el-button>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>应用</template>
          <div class="text-gray-500 mb-10px">创建应用 / 获取 ApiKey</div>
          <el-button type="primary" @click="go('/apex/apps')">进入应用管理</el-button>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>订阅</template>
          <div class="text-gray-500 mb-10px">申请订阅 / 审核通过</div>
          <el-button type="primary" @click="go('/apex/subscriptions')">进入订阅管理</el-button>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>治理</template>
          <div class="text-gray-500 mb-10px">策略管理 / 启停用</div>
          <el-button type="primary" @click="go('/apex/policies')">进入治理策略</el-button>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>监控</template>
          <div class="text-gray-500 mb-10px">调用趋势 / 指标查看</div>
          <el-button type="primary" @click="go('/apex/metrics')">进入监控指标</el-button>
        </el-card>
      </el-col>
    </el-row>
  </ContentWrap>
</template>

<script setup lang="ts">
import ContentWrap from '@/components/ContentWrap/src/ContentWrap.vue'

defineOptions({ name: 'ApexIndex' })

const router = useRouter()
const go = (path: string) => router.push(path)
</script>
