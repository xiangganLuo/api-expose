<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="68px"
    >
      <el-form-item label="配置键" prop="configKey">
        <el-input
          v-model="queryParams.configKey"
          placeholder="请输入配置键"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="配置名称" prop="configName">
        <el-input
          v-model="queryParams.configName"
          placeholder="请输入配置名称"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="业务类型" prop="businessType">
        <el-input
          v-model="queryParams.businessType"
          placeholder="请输入业务类型"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable class="!w-240px">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['system:data-permission-config:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['system:data-permission-config:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="ID" align="center" prop="id" />
      <el-table-column label="配置键" align="center" prop="configKey" />
      <el-table-column label="配置名称" align="center" prop="configName" />
      <el-table-column label="业务类型" align="center" prop="businessType" />
      <el-table-column label="不受限角色" align="center" prop="roleIds" width="200">
        <template #default="scope">
          <div v-if="scope.row.roleIds && scope.row.roleIds.length > 0">
            <el-tag v-for="roleId in scope.row.roleIds" :key="roleId" size="small" class="mr-1 mb-1">
              {{ getRoleName(roleId) }}
            </el-tag>
          </div>
          <span v-else class="text-gray-400">-</span>
        </template>
      </el-table-column>
      <el-table-column label="过滤字段" align="center" prop="filterColumn" />
      <el-table-column label="过滤表名" align="center" prop="filterTable" />
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" width="200" show-overflow-tooltip />
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        width="180"
        :formatter="dateFormatter"
      />
      <el-table-column label="操作" align="center" width="180">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['system:data-permission-config:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['system:data-permission-config:delete']"
          >
            删除
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

  <!-- 表单弹窗：添加/修改 -->
  <DataPermissionConfigForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import {dateFormatter} from '@/utils/formatTime'
import download from '@/utils/download'
import * as DataPermissionConfigApi from '@/api/system/dataPermissionConfig'
import {DataPermissionConfigVO} from '@/api/system/dataPermissionConfig'
import DataPermissionConfigForm from './DataPermissionConfigForm.vue'
import {DICT_TYPE, getIntDictOptions} from '@/utils/dict'
import {getSimpleRoleList, RoleVO} from '@/api/system/role'

/** 数据权限配置 列表 */
defineOptions({ name: 'DataPermissionConfig' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<DataPermissionConfigVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  configKey: undefined,
  configName: undefined,
  businessType: undefined,
  status: undefined
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

// 角色数据
const roleList = ref<RoleVO[]>([])

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await DataPermissionConfigApi.getDataPermissionConfigPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

/** 添加/修改操作 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await DataPermissionConfigApi.deleteDataPermissionConfig(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await DataPermissionConfigApi.exportDataPermissionConfigExcel(queryParams)
    download.excel(data, '数据权限配置.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 获取角色名称 */
const getRoleName = (roleId: number): string => {
  if (!roleList.value || roleList.value.length === 0) {
    return `角色${roleId}` // 角色数据还未加载完成时的临时显示
  }
  const role = roleList.value.find(r => r.id === roleId)
  return role ? role.name : `未知角色(${roleId})`
}

/** 初始化 **/
onMounted(async () => {
  await getList()
  // 加载角色列表
  try {
    roleList.value = await getSimpleRoleList()
  } catch {
    // 加载角色列表失败不影响主功能
  }
})
</script>
