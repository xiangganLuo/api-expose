<template>
  <!-- 搜索工作栏 -->
  <ContentWrap>
    <el-form class="-mb-15px" :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
      <el-form-item label="部门名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入部门名称"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="部门状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择部门状态"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="部门类型" prop="deptType">
        <el-select
          v-model="queryParams.deptType"
          placeholder="请选择部门类型"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.SYSTEM_DEPT_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery">
          <Icon icon="ep:search" class="mr-5px" /> 搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon icon="ep:refresh" class="mr-5px" /> 重置
        </el-button>
        <el-button
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['system:dept:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['system:dept:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
        <el-button type="danger" plain @click="toggleExpandAll">
          <Icon icon="ep:sort" class="mr-5px" /> 展开/折叠
        </el-button>
        <el-button
          type="danger"
          plain
          :disabled="checkedIds.length === 0"
          @click="handleDeleteBatch"
          v-hasPermi="['system:dept:delete']"
        >
          <Icon icon="ep:delete" class="mr-5px" /> 批量删除
        </el-button>
        <el-button
          type="warning"
          plain
          @click="handleFixDeptLevelNames"
          :loading="fixLoading"
          v-hasPermi="['system:dept:update']"
        >
          <Icon icon="ep:tools" class="mr-5px" /> 修复层级全称
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table
      v-loading="loading"
      :data="list"
      row-key="id"
      :default-expand-all="isExpandAll"
      v-if="refreshTable"
      @selection-change="handleRowCheckboxChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="name" label="部门名称" min-width="200" show-overflow-tooltip />
      <el-table-column prop="deptLevelName" label="部门层级全称" min-width="250" show-overflow-tooltip />
      <el-table-column prop="deptType" label="部门类型" width="100" align="center">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.SYSTEM_DEPT_TYPE" :value="scope.row.deptType" />
        </template>
      </el-table-column>
      <el-table-column label="负责人" prop="leaderUserNames">
        <template #default="scope">
          <span v-if="Array.isArray(scope.row.leaderUserNames)">
            {{ scope.row.leaderUserNames.join('，') }}
          </span>
          <span v-else>
            {{ scope.row.leaderUserNames }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="sort" label="排序" width="60" align="center" />
      <el-table-column prop="status" label="状态" width="80" align="center">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180" :formatter="dateFormatter" />
      <el-table-column label="操作" align="center">
        <template #default="scope">
          <el-button link type="primary" @click="openForm('update', scope.row.id)" v-hasPermi="['system:dept:update']">
            修改
          </el-button>
          <el-button link type="danger" @click="handleDelete(scope.row.id)" v-hasPermi="['system:dept:delete']">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </ContentWrap>

  <!-- 表单弹窗：添加/修改 -->
  <DeptForm ref="formRef" @success="getList" />
</template>
<script lang="ts" setup>
import {DICT_TYPE, getIntDictOptions, getStrDictOptions} from '@/utils/dict'
import {dateFormatter} from '@/utils/formatTime'
import {handleTree} from '@/utils/tree'
import * as DeptApi from '@/api/system/dept'
import DeptForm from './DeptForm.vue'
import * as UserApi from '@/api/system/user'
import download from '@/utils/download'

defineOptions({ name: 'SystemDept' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref() // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 100,
  name: undefined,
  status: undefined,
  deptType: undefined
})
const queryFormRef = ref() // 搜索的表单
const isExpandAll = ref(true) // 是否展开，默认全部展开
const refreshTable = ref(true) // 重新渲染表格状态
const userList = ref<UserApi.UserVO[]>([]) // 用户列表
const exportLoading = ref(false) // 导出的加载中
const fixLoading = ref(false) // 修复的加载中

/** 查询部门列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await DeptApi.getDeptPage(queryParams)
    list.value = handleTree(data)
  } finally {
    loading.value = false
  }
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    exportLoading.value = true
    const data = await DeptApi.exportDept(queryParams)
    download.excel(data, '部门列表.xls')
  } finally {
    exportLoading.value = false
  }
}

/** 展开/折叠操作 */
const toggleExpandAll = () => {
  refreshTable.value = false
  isExpandAll.value = !isExpandAll.value
  nextTick(() => {
    refreshTable.value = true
  })
}

/** 搜索按钮操作 */
const handleQuery = () => {
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryParams.pageNo = 1
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
    await DeptApi.deleteDept(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch { }
}

/** 批量删除按钮操作 */
const checkedIds = ref<number[]>([])
const handleRowCheckboxChange = (rows: DeptApi.DeptVO[]) => {
  checkedIds.value = rows.map((row) => row.id)
}

const handleDeleteBatch = async () => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起批量删除
    await DeptApi.deleteDeptList(checkedIds.value)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch { }
}

/** 修复部门层级全称 */
const handleFixDeptLevelNames = async () => {
  try {
    // 确认操作
    await message.confirm('此操作将为所有部门重新计算并填充层级全称，是否继续？', '修复确认')
    fixLoading.value = true
    // 发起修复请求
    await DeptApi.fixDeptLevelNames()
    message.success('部门层级全称修复成功')
    // 刷新列表
    await getList()
  } catch {
  } finally {
    fixLoading.value = false
  }
}

/** 初始化 **/
onMounted(async () => {
  await getList()
  // 获取用户列表
  userList.value = await UserApi.getSimpleUserList()
})
</script>
