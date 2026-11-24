<template>
  <div class="users-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">用户管理</h1>
        <p class="page-subtitle">管理平台所有用户信息</p>
      </div>
      <el-button type="primary" class="add-btn" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        <span>添加用户</span>
      </el-button>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <div class="search-form-wrapper">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="姓名">
            <el-input 
              v-model="searchForm.name" 
              placeholder="请输入姓名" 
              clearable
              prefix-icon="User"
              class="search-input"
            />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input 
              v-model="searchForm.phone" 
              placeholder="请输入手机号" 
              clearable
              prefix-icon="Phone"
              class="search-input"
            />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable class="search-select">
              <el-option label="正常" :value="1">
                <el-tag type="success" size="small" effect="light">正常</el-tag>
              </el-option>
              <el-option label="禁用" :value="0">
                <el-tag type="danger" size="small" effect="light">禁用</el-tag>
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" class="search-btn" @click="handleSearch">
              <el-icon><Search /></el-icon>
              <span>搜索</span>
            </el-button>
            <el-button class="reset-btn" @click="handleReset">
              <el-icon><RefreshRight /></el-icon>
              <span>重置</span>
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-section">
      <div class="table-card">
        <el-table 
          :data="userList" 
          v-loading="loading" 
          style="width: 100%"
          :header-cell-style="headerCellStyle"
          row-class-name="table-row"
        >
          <el-table-column type="index" label="#" width="60" align="center" />
          <el-table-column label="用户信息" min-width="200">
            <template #default="{ row }">
              <div class="user-cell">
                <el-avatar :size="40" :src="row.avatar" class="user-avatar">
                  {{ row.name?.charAt(0) || 'U' }}
                </el-avatar>
                <div class="user-info">
                  <div class="user-name">{{ row.name }}</div>
                  <div class="user-phone">
                    <el-icon><Phone /></el-icon>
                    {{ row.phone }}
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="性别" width="80" align="center">
            <template #default="{ row }">
              <div class="gender-tag" :class="row.gender === 1 ? 'male' : 'female'">
                <el-icon>
                  <Male v-if="row.gender === 1" />
                  <Female v-else />
                </el-icon>
                <span>{{ row.gender === 1 ? '男' : '女' }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip>
            <template #default="{ row }">
              <div class="address-cell">
                <el-icon><Location /></el-icon>
                <span>{{ row.address || '未填写' }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="紧急联系人" min-width="180">
            <template #default="{ row }">
              <div class="emergency-cell">
                <div class="contact-name">{{ row.emergencyContact || '未设置' }}</div>
                <div class="contact-phone" v-if="row.emergencyPhone">
                  <el-icon><Phone /></el-icon>
                  {{ row.emergencyPhone }}
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">
              <div class="status-switch-wrapper">
                <el-switch 
                  v-model="row.status" 
                  :active-value="1" 
                  :inactive-value="0"
                  @change="(val) => handleStatusChange(row, val)"
                />
                <span class="status-text" :class="row.status === 1 ? 'active' : 'inactive'">
                  {{ row.status === 1 ? '正常' : '禁用' }}
                </span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="注册时间" width="160" align="center">
            <template #default="{ row }">
              <div class="time-cell">
                <el-icon><Clock /></el-icon>
                <span>{{ formatTime(row.createTime) }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right" align="center">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-tooltip content="查看详情" placement="top">
                  <el-button type="primary" link class="action-btn view" @click="handleView(row)">
                    <el-icon><View /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip content="编辑" placement="top">
                  <el-button type="primary" link class="action-btn edit" @click="handleEdit(row)">
                    <el-icon><Edit /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip content="删除" placement="top">
                  <el-button type="danger" link class="action-btn delete" @click="handleDelete(row)">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </el-tooltip>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <!-- 空状态 -->
        <div v-if="userList.length === 0 && !loading" class="empty-state">
          <el-icon :size="64"><User /></el-icon>
          <p>暂无用户数据</p>
          <el-button type="primary" @click="handleAdd">添加用户</el-button>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          class="modern-pagination"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 用户表单对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle" 
      width="600px"
      class="user-dialog"
      destroy-on-close
    >
      <div class="dialog-content">
        <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px" class="user-form">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="姓名" prop="name">
                <el-input v-model="formData.name" placeholder="请输入姓名" prefix-icon="User" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="formData.phone" placeholder="请输入手机号" prefix-icon="Phone" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="性别" prop="gender">
                <el-radio-group v-model="formData.gender" class="gender-radio">
                  <el-radio-button :label="1">
                    <el-icon><Male /></el-icon>
                    男
                  </el-radio-button>
                  <el-radio-button :label="2">
                    <el-icon><Female /></el-icon>
                    女
                  </el-radio-button>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="出生日期" prop="birthday">
                <el-date-picker 
                  v-model="formData.birthday" 
                  type="date" 
                  placeholder="请选择出生日期" 
                  value-format="YYYY-MM-DD"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="地址" prop="address">
            <el-input 
              v-model="formData.address" 
              type="textarea" 
              :rows="2"
              placeholder="请输入地址"
              prefix-icon="Location"
            />
          </el-form-item>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="紧急联系人" prop="emergencyContact">
                <el-input v-model="formData.emergencyContact" placeholder="请输入紧急联系人" prefix-icon="UserFilled" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="紧急电话" prop="emergencyPhone">
                <el-input v-model="formData.emergencyPhone" placeholder="请输入紧急联系电话" prefix-icon="Phone" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="状态" prop="status">
            <el-switch 
              v-model="formData.status" 
              :active-value="1" 
              :inactive-value="0"
              active-text="正常"
              inactive-text="禁用"
              inline-prompt
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button class="cancel-btn" @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" class="submit-btn" @click="handleSubmit">
            <el-icon><Check /></el-icon>
            <span>确定</span>
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 用户详情抽屉 -->
    <el-drawer
      v-model="drawerVisible"
      title="用户详情"
      size="450px"
      class="user-drawer"
    >
      <div class="user-detail" v-if="currentUser">
        <div class="detail-header">
          <el-avatar :size="80" :src="currentUser.avatar" class="detail-avatar">
            {{ currentUser.name?.charAt(0) || 'U' }}
          </el-avatar>
          <h3 class="detail-name">{{ currentUser.name }}</h3>
          <div class="detail-status">
            <el-tag :type="currentUser.status === 1 ? 'success' : 'danger'" effect="dark">
              {{ currentUser.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </div>
        </div>

        <div class="detail-section">
          <h4 class="section-title">
            <el-icon><User /></el-icon>
            基本信息
          </h4>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="item-label">手机号</span>
              <span class="item-value">{{ currentUser.phone }}</span>
            </div>
            <div class="detail-item">
              <span class="item-label">性别</span>
              <span class="item-value">
                <el-icon><Male v-if="currentUser.gender === 1" /><Female v-else /></el-icon>
                {{ currentUser.gender === 1 ? '男' : '女' }}
              </span>
            </div>
            <div class="detail-item">
              <span class="item-label">出生日期</span>
              <span class="item-value">{{ currentUser.birthday || '未填写' }}</span>
            </div>
            <div class="detail-item">
              <span class="item-label">注册时间</span>
              <span class="item-value">{{ currentUser.createTime }}</span>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h4 class="section-title">
            <el-icon><Location /></el-icon>
            地址信息
          </h4>
          <p class="detail-address">{{ currentUser.address || '未填写' }}</p>
        </div>

        <div class="detail-section">
          <h4 class="section-title">
            <el-icon><Phone /></el-icon>
            紧急联系人
          </h4>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="item-label">联系人</span>
              <span class="item-value">{{ currentUser.emergencyContact || '未设置' }}</span>
            </div>
            <div class="detail-item">
              <span class="item-label">联系电话</span>
              <span class="item-value">{{ currentUser.emergencyPhone || '未设置' }}</span>
            </div>
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, createUser, updateUser, deleteUser } from '@/api/user'
import { 
  Plus, Search, RefreshRight, User, Phone, Clock,
  Male, Female, Location, View, Edit, Delete, Check,
  UserFilled
} from '@element-plus/icons-vue'

const loading = ref(false)
const dialogVisible = ref(false)
const drawerVisible = ref(false)
const dialogTitle = ref('查看用户')
const formRef = ref(null)
const currentUser = ref(null)

const searchForm = reactive({
  name: '',
  phone: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const userList = ref([])

const formData = reactive({
  id: null,
  name: '',
  phone: '',
  gender: 1,
  birthday: '',
  address: '',
  emergencyContact: '',
  emergencyPhone: '',
  status: 1
})

const formRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  emergencyPhone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const headerCellStyle = {
  background: '#f8fafc',
  color: '#475569',
  fontWeight: 600,
  fontSize: '13px'
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const handleSearch = () => {
  pagination.page = 1
  loadUsers()
}

const handleReset = () => {
  searchForm.name = ''
  searchForm.phone = ''
  searchForm.status = ''
  handleSearch()
}

const handleSizeChange = (val) => {
  pagination.pageSize = val
  loadUsers()
}

const handleCurrentChange = (val) => {
  pagination.page = val
  loadUsers()
}

const handleStatusChange = async (row, val) => {
  try {
    await updateUser(row.id, { ...row, status: val })
    ElMessage.success(val === 1 ? '用户已启用' : '用户已禁用')
  } catch (error) {
    row.status = val === 1 ? 0 : 1
    ElMessage.error('操作失败')
  }
}

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await getUserList({
      page: pagination.page,
      pageSize: pagination.pageSize,
      ...searchForm
    })
    userList.value = res.list || []
    pagination.total = res.total || 0
  } catch (error) {
    console.error('加载用户列表失败', error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  dialogTitle.value = '添加用户'
  Object.assign(formData, {
    id: null,
    name: '',
    phone: '',
    gender: 1,
    birthday: '',
    address: '',
    emergencyContact: '',
    emergencyPhone: '',
    status: 1
  })
  dialogVisible.value = true
}

const handleView = (row) => {
  currentUser.value = row
  drawerVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑用户'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除用户"${row.name}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    customClass: 'custom-message-box'
  }).then(async () => {
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    loadUsers()
  }).catch(() => {})
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (formData.id) {
        await updateUser(formData.id, formData)
        ElMessage.success('编辑成功')
        dialogVisible.value = false
        loadUsers()
      } else {
        await createUser(formData)
        ElMessage.success('添加成功')
        dialogVisible.value = false
        loadUsers()
      }
    }
  })
}

onMounted(() => {
  loadUsers()
})
</script>

<style lang="scss" scoped>
.users-page {
  padding: 0;
}

// 页面标题
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 0 4px;

  .header-content {
    .page-title {
      font-size: 28px;
      font-weight: 700;
      color: #1e293b;
      margin: 0 0 4px;
    }

    .page-subtitle {
      font-size: 14px;
      color: #64748b;
      margin: 0;
    }
  }

  .add-btn {
    border-radius: 12px;
    padding: 12px 24px;
    font-weight: 500;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: none;
    box-shadow: 0 4px 14px rgba(102, 126, 234, 0.4);

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
    }

    .el-icon {
      margin-right: 6px;
    }
  }
}

// 搜索区域
.search-section {
  margin-bottom: 24px;

  .search-form-wrapper {
    background: #fff;
    border-radius: 16px;
    padding: 20px 24px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  }

  .search-form {
    :deep(.el-form-item) {
      margin-bottom: 0;
      margin-right: 16px;

      .el-form-item__label {
        font-weight: 500;
        color: #475569;
      }
    }

    .search-input,
    .search-select {
      width: 200px;
    }

    .search-btn {
      border-radius: 10px;
      padding: 10px 20px;
      font-weight: 500;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border: none;

      .el-icon {
        margin-right: 4px;
      }
    }

    .reset-btn {
      border-radius: 10px;
      padding: 10px 20px;
      font-weight: 500;

      .el-icon {
        margin-right: 4px;
      }
    }
  }
}

// 表格区域
.table-section {
  .table-card {
    background: #fff;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
    overflow: hidden;
  }

  :deep(.el-table) {
    border-radius: 12px;
    overflow: hidden;

    .el-table__header-wrapper {
      th {
        background: #f8fafc !important;
        color: #475569;
        font-weight: 600;
        font-size: 13px;
        padding: 16px 12px;
      }
    }

    .el-table__body-wrapper {
      td {
        padding: 16px 12px;
      }

      .table-row {
        transition: all 0.3s ease;

        &:hover {
          background: #f8fafc;
        }
      }
    }
  }

  // 用户单元格
  .user-cell {
    display: flex;
    align-items: center;
    gap: 12px;

    .user-avatar {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: #fff;
      font-weight: 600;
    }

    .user-info {
      .user-name {
        font-weight: 600;
        color: #1e293b;
        font-size: 14px;
        margin-bottom: 4px;
      }

      .user-phone {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 12px;
        color: #64748b;

        .el-icon {
          font-size: 14px;
        }
      }
    }
  }

  // 性别标签
  .gender-tag {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    padding: 6px 12px;
    border-radius: 20px;
    font-size: 13px;
    font-weight: 500;

    &.male {
      background: #dbeafe;
      color: #3b82f6;
    }

    &.female {
      background: #fce7f3;
      color: #ec4899;
    }

    .el-icon {
      font-size: 14px;
    }
  }

  // 地址单元格
  .address-cell {
    display: flex;
    align-items: center;
    gap: 6px;
    color: #64748b;
    font-size: 13px;

    .el-icon {
      color: #94a3b8;
      flex-shrink: 0;
    }
  }

  // 紧急联系人
  .emergency-cell {
    .contact-name {
      font-weight: 500;
      color: #1e293b;
      font-size: 13px;
      margin-bottom: 2px;
    }

    .contact-phone {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 12px;
      color: #64748b;

      .el-icon {
        font-size: 12px;
      }
    }
  }

  // 状态切换
  .status-switch-wrapper {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;

    .status-text {
      font-size: 12px;
      font-weight: 500;

      &.active {
        color: #10b981;
      }

      &.inactive {
        color: #ef4444;
      }
    }
  }

  // 时间单元格
  .time-cell {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    color: #64748b;
    font-size: 13px;

    .el-icon {
      color: #94a3b8;
    }
  }

  // 操作按钮
  .action-buttons {
    display: flex;
    justify-content: center;
    gap: 8px;

    .action-btn {
      padding: 8px;
      border-radius: 8px;
      transition: all 0.3s ease;

      &.view {
        color: #3b82f6;
        background: #dbeafe;

        &:hover {
          background: #3b82f6;
          color: #fff;
        }
      }

      &.edit {
        color: #f59e0b;
        background: #fef3c7;

        &:hover {
          background: #f59e0b;
          color: #fff;
        }
      }

      &.delete {
        color: #ef4444;
        background: #fee2e2;

        &:hover {
          background: #ef4444;
          color: #fff;
        }
      }

      .el-icon {
        font-size: 16px;
      }
    }
  }

  // 空状态
  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 20px;
    color: #94a3b8;

    .el-icon {
      margin-bottom: 16px;
      color: #cbd5e1;
    }

    p {
      margin: 0 0 20px;
      font-size: 14px;
    }
  }
}

// 分页
.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;

  .modern-pagination {
    background: #fff;
    padding: 12px 24px;
    border-radius: 12px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

    :deep(.el-pagination__total) {
      color: #64748b;
    }

    :deep(.el-pagination__sizes) {
      .el-input__inner {
        border-radius: 8px;
      }
    }

    :deep(.el-pager) {
      li {
        border-radius: 8px;
        font-weight: 500;

        &.active {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }

        &:hover:not(.active) {
          color: #667eea;
        }
      }
    }
  }
}

// 对话框
:deep(.user-dialog) {
  .el-dialog__header {
    padding: 20px 24px;
    border-bottom: 1px solid #f1f5f9;
    margin-right: 0;

    .el-dialog__title {
      font-weight: 600;
      color: #1e293b;
    }
  }

  .el-dialog__body {
    padding: 24px;
  }

  .dialog-content {
    .user-form {
      .el-form-item {
        margin-bottom: 20px;

        .el-form-item__label {
          font-weight: 500;
          color: #475569;
        }

        .el-input__wrapper {
          border-radius: 10px;
        }
      }

      .gender-radio {
        .el-radio-button__inner {
          padding: 8px 20px;
          display: flex;
          align-items: center;
          gap: 4px;

          .el-icon {
            font-size: 14px;
          }
        }
      }
    }
  }

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    padding-top: 20px;
    border-top: 1px solid #f1f5f9;

    .cancel-btn {
      border-radius: 10px;
      padding: 10px 24px;
    }

    .submit-btn {
      border-radius: 10px;
      padding: 10px 24px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border: none;

      .el-icon {
        margin-right: 4px;
      }
    }
  }
}

// 抽屉
:deep(.user-drawer) {
  .el-drawer__header {
    padding: 20px 24px;
    border-bottom: 1px solid #f1f5f9;
    margin-bottom: 0;

    span {
      font-weight: 600;
      color: #1e293b;
      font-size: 18px;
    }
  }

  .el-drawer__body {
    padding: 24px;
    background: #f8fafc;
  }

  .user-detail {
    .detail-header {
      text-align: center;
      padding: 20px;
      background: #fff;
      border-radius: 16px;
      margin-bottom: 20px;

      .detail-avatar {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: #fff;
        font-size: 32px;
        font-weight: 600;
        margin-bottom: 12px;
      }

      .detail-name {
        font-size: 20px;
        font-weight: 600;
        color: #1e293b;
        margin: 0 0 12px;
      }

      .detail-status {
        .el-tag {
          border-radius: 20px;
          padding: 6px 16px;
        }
      }
    }

    .detail-section {
      background: #fff;
      border-radius: 16px;
      padding: 20px;
      margin-bottom: 16px;

      .section-title {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 14px;
        font-weight: 600;
        color: #475569;
        margin: 0 0 16px;
        padding-bottom: 12px;
        border-bottom: 1px solid #f1f5f9;

        .el-icon {
          color: #667eea;
        }
      }

      .detail-grid {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 16px;

        .detail-item {
          display: flex;
          flex-direction: column;
          gap: 4px;

          .item-label {
            font-size: 12px;
            color: #94a3b8;
          }

          .item-value {
            font-size: 14px;
            font-weight: 500;
            color: #1e293b;
            display: flex;
            align-items: center;
            gap: 4px;

            .el-icon {
              color: #64748b;
            }
          }
        }
      }

      .detail-address {
        margin: 0;
        font-size: 14px;
        color: #64748b;
        line-height: 1.6;
      }
    }
  }
}

// 响应式
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .search-section {
    .search-form-wrapper {
      padding: 16px;
    }

    .search-form {
      :deep(.el-form-item) {
        margin-right: 0;
        margin-bottom: 12px;
        width: 100%;

        .search-input,
        .search-select {
          width: 100%;
        }
      }
    }
  }
}
</style>