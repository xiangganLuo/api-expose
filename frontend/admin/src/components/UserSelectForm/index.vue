<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="1200px" class="user-select-dialog">
    <!-- 搜索区域 -->
    <div class="search-container">
      <el-form
        :model="queryParams"
        ref="queryFormRef"
        :inline="true"
        label-width="80px"
        class="search-form"
      >
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="用户名称" prop="username">
              <el-input
                v-model="queryParams.username"
                placeholder="请输入用户名称"
                clearable
                @keyup.enter="handleQuery"
                class="w-full"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="手机号码" prop="mobile">
              <el-input
                v-model="queryParams.mobile"
                placeholder="请输入手机号码"
                clearable
                @keyup.enter="handleQuery"
                class="w-full"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item class="search-buttons">
              <el-button type="primary" @click="handleQuery">
                <Icon icon="ep:search" class="mr-5px" />
                搜索
              </el-button>
              <el-button @click="resetQuery">
                <Icon icon="ep:refresh" class="mr-5px" />
                重置
              </el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <!-- 用户列表 -->
    <div class="table-container">
      <el-table
        v-loading="loading"
        :data="userList"
        @selection-change="handleSelectionChange"
        @current-change="handleCurrentChange"
        ref="tableRef"
        class="user-table"
        stripe
        height="400"
      >
        <el-table-column v-if="multiple" type="selection" width="50" align="center" />
        <el-table-column v-else width="50" align="center">
          <template #default="scope">
            <el-radio
              v-model="selectedUserId"
              :label="scope.row.id"
              @change="handleRadioChange(scope.row)"
            >
              &nbsp;
            </el-radio>
          </template>
        </el-table-column>
        <el-table-column label="用户编号" align="center" prop="id" width="120" />
        <el-table-column
          label="用户名称"
          align="center"
          prop="username"
          :show-overflow-tooltip="true"
        />
        <el-table-column
          label="用户昵称"
          align="center"
          prop="nickname"
          :show-overflow-tooltip="true"
        />
        <el-table-column
          label="部门"
          align="center"
          prop="deptName"
          :show-overflow-tooltip="true"
        />
        <el-table-column label="手机号码" align="center" prop="mobile" width="120" />
        <el-table-column label="状态" align="center" prop="status" width="100">
          <template #default="scope">
            <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column
          label="创建时间"
          align="center"
          prop="createTime"
          width="180"
          :formatter="dateFormatter"
        />
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-container">
      <Pagination
        :total="total"
        v-model:page="queryParams.pageNo"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button :disabled="!hasSelection" type="primary" @click="handleConfirm">
          确 定
        </el-button>
        <el-button @click="dialogVisible = false"> 取 消 </el-button>
      </div>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import {dateFormatter} from '@/utils/formatTime'
import {DICT_TYPE} from '@/utils/dict'
import * as UserApi from '@/api/system/user'

defineOptions({ name: 'UserSelectForm' })

interface Props {
  multiple?: boolean // 是否支持多选，默认为单选
}

const props = withDefaults(defineProps<Props>(), {
  multiple: false
})

const emit = defineEmits(['confirm'])

const dialogVisible = ref(false)
const dialogTitle = ref('选择用户')
const loading = ref(true)
const total = ref(0)
const userList = ref<UserApi.UserVO[]>([])
const queryFormRef = ref()
const tableRef = ref()

// 选择相关
const selectedUsers = ref<UserApi.UserVO[]>([])
const selectedUserId = ref<number>()

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  username: undefined,
  mobile: undefined,
  status: undefined,
  deptId: undefined
})

const hasSelection = computed(() => {
  return props.multiple ? selectedUsers.value.length > 0 : selectedUserId.value !== undefined
})

/** 查询用户列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await UserApi.getUserPage(queryParams)
    userList.value = data.list
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
  queryFormRef.value?.resetFields()
  handleQuery()
}

/** 多选框选中数据 */
const handleSelectionChange = (selection: UserApi.UserVO[]) => {
  selectedUsers.value = selection
}

/** 单选框选中数据 */
const handleCurrentChange = (currentRow: UserApi.UserVO) => {
  if (!props.multiple) {
    selectedUserId.value = currentRow?.id
  }
}

/** 单选按钮变化 */
const handleRadioChange = (row: UserApi.UserVO) => {
  selectedUserId.value = row.id
}

/** 确认选择 */
const handleConfirm = () => {
  if (props.multiple) {
    emit('confirm', selectedUsers.value)
  } else {
    const selectedUser = userList.value.find((user) => user.id === selectedUserId.value)
    if (selectedUser) {
      emit('confirm', [selectedUser])
    }
  }
  dialogVisible.value = false
}

/** 打开弹窗 */
const open = () => {
  dialogVisible.value = true
  selectedUsers.value = []
  selectedUserId.value = undefined
  resetQuery()
}

/** 重置选择 */
const resetSelection = () => {
  selectedUsers.value = []
  selectedUserId.value = undefined
  if (tableRef.value) {
    tableRef.value.clearSelection()
  }
}

defineExpose({ open, resetSelection })

onMounted(() => {
  getList()
})
</script>

<style scoped>
.user-select-dialog {
  .search-form {
    .search-buttons {
      display: flex;
      justify-content: flex-end;
      align-items: center;
    }
  }

  .table-container {
    .user-table {
      border-radius: 6px;
      overflow: hidden;
    }
  }

  .pagination-container {
    display: flex;
    justify-content: right;
  }

  .dialog-footer {
    display: flex;
    justify-content: right;
    gap: 10px;

    .el-button {
      min-width: 80px;
    }
  }
}
/* 表格选择行高亮 */
.user-select-dialog :deep(.el-table__row.current-row) {
  background-color: #ecf5ff !important;
}

.user-select-dialog :deep(.el-table__row.current-row:hover) {
  background-color: #d9ecff !important;
}

/* 优化加载状态 */
.user-select-dialog :deep(.el-loading-mask) {
  border-radius: 6px;
}
</style>
