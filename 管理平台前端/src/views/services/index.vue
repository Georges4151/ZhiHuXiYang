<template>
  <div class="services-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">服务管理</h1>
        <p class="page-subtitle">管理平台所有服务项目</p>
      </div>
      <div class="header-stats">
        <div class="stat-item">
          <span class="stat-value">{{ stats.total }}</span>
          <span class="stat-label">服务总数</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ stats.active }}</span>
          <span class="stat-label">已上架</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ stats.sales }}</span>
          <span class="stat-label">总销量</span>
        </div>
      </div>
    </div>
    
    <!-- 搜索区域 -->
    <div class="search-section">
      <div class="search-form-wrapper">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="服务名称">
            <el-input 
              v-model="searchForm.name" 
              placeholder="请输入服务名称" 
              clearable
              prefix-icon="Service"
              class="search-input"
            />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable class="search-select">
              <el-option label="已上架" :value="1">
                <el-tag type="success" size="small" effect="light">已上架</el-tag>
              </el-option>
              <el-option label="已下架" :value="0">
                <el-tag type="info" size="small" effect="light">已下架</el-tag>
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
        <el-button type="primary" class="add-btn" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          <span>添加服务</span>
        </el-button>
      </div>
    </div>
    
    <!-- 数据表格 -->
    <div class="table-section">
      <div class="table-card">
        <el-table 
          :data="serviceList" 
          v-loading="loading" 
          style="width: 100%"
          :header-cell-style="headerCellStyle"
          row-class-name="table-row"
        >
          <el-table-column type="index" label="#" width="60" align="center" />
          <el-table-column label="服务信息" min-width="220">
            <template #default="{ row }">
              <div class="service-cell">
                <div class="service-icon" :class="{ 'active': row.status === 1 }">
                  <el-icon><Service /></el-icon>
                </div>
                <div class="service-info">
                  <div class="service-name">{{ row.name }}</div>
                  <div class="service-desc">{{ row.description || '暂无描述' }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="价格" width="120" align="center">
            <template #default="{ row }">
              <div class="price-tag">
                <span class="price-symbol">¥</span>
                <span class="price-value">{{ row.price }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="服务时长" width="100" align="center">
            <template #default="{ row }">
              <div class="duration-info">
                <el-icon><Timer /></el-icon>
                <span>{{ row.duration || '-' }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="销量/评分" width="150" align="center">
            <template #default="{ row }">
              <div class="stats-info">
                <div class="sales-count">
                  <el-icon><ShoppingCart /></el-icon>
                  <span>{{ row.sales || 0 }}</span>
                </div>
                <div class="rating-stars">
                  <el-rate 
                    v-model="row.rating" 
                    disabled 
                    show-score 
                    text-color="#ff9900"
                    :max="5"
                    size="small"
                  />
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">
              <div class="status-wrapper" @click="handleToggleStatus(row)">
                <div class="status-indicator" :class="row.status === 1 ? 'active' : 'inactive'">
                  <div class="status-dot"></div>
                  <span class="status-text">{{ row.status === 1 ? '已上架' : '已下架' }}</span>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right" align="center">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-tooltip content="编辑" placement="top">
                  <el-button 
                    type="primary" 
                    circle
                    class="action-btn edit"
                    @click="handleEdit(row)"
                  >
                    <el-icon><Edit /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip :content="row.status === 1 ? '下架' : '上架'" placement="top">
                  <el-button 
                    :type="row.status === 1 ? 'warning' : 'success'" 
                    circle
                    class="action-btn toggle"
                    @click="handleToggleStatus(row)"
                  >
                    <el-icon>
                      <component :is="row.status === 1 ? 'Bottom' : 'Top'" />
                    </el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip content="删除" placement="top">
                  <el-button 
                    type="danger" 
                    circle
                    class="action-btn delete"
                    @click="handleDelete(row)"
                  >
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </el-tooltip>
              </div>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 分页 -->
        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            class="pagination"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>
    
    <!-- 添加/编辑对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle" 
      width="600px"
      class="service-dialog"
      destroy-on-close
    >
      <div class="dialog-content">
        <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px" class="service-form">
          <el-form-item label="服务名称" prop="name">
            <el-input v-model="formData.name" placeholder="请输入服务名称" class="form-input">
              <template #prefix>
                <el-icon><Service /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="价格" prop="price">
            <el-input-number v-model="formData.price" :min="0" :precision="2" class="form-number">
              <template #prefix>
                <span class="currency">¥</span>
              </template>
            </el-input-number>
          </el-form-item>
          <el-form-item label="服务描述" prop="description">
            <el-input 
              v-model="formData.description" 
              type="textarea" 
              :rows="3" 
              placeholder="请输入服务描述"
              class="form-textarea"
              resize="none"
            />
          </el-form-item>
          <el-form-item label="服务时长" prop="duration">
            <el-input v-model="formData.duration" placeholder="如：2小时" class="form-input">
              <template #prefix>
                <el-icon><Timer /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="排序" prop="sort">
            <el-input-number v-model="formData.sort" :min="0" class="form-number" />
          </el-form-item>
          <el-form-item label="状态">
            <div class="status-switch-wrapper">
              <el-switch 
                v-model="formData.status" 
                :active-value="1" 
                :inactive-value="0"
                active-text="上架"
                inactive-text="下架"
                inline-prompt
                class="status-switch"
              />
              <span class="switch-hint">{{ formData.status === 1 ? '服务将在前端展示' : '服务将隐藏' }}</span>
            </div>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getServiceList, addService, updateService, deleteService } from '@/api/service'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('添加服务')
const formRef = ref(null)

const searchForm = reactive({
  name: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const serviceList = ref([])

const formData = reactive({
  id: null,
  name: '',
  price: 0,
  description: '',
  duration: '',
  sort: 0,
  status: 1
})

const formRules = {
  name: [{ required: true, message: '请输入服务名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

// 统计数据
const stats = computed(() => {
  return {
    total: serviceList.value.length,
    active: serviceList.value.filter(item => item.status === 1).length,
    sales: serviceList.value.reduce((sum, item) => sum + (item.sales || 0), 0)
  }
})

// 表格头部样式
const headerCellStyle = () => {
  return {
    background: '#f8fafc',
    color: '#475569',
    fontWeight: 600,
    fontSize: '14px',
    padding: '16px 0'
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadServices()
}

const handleReset = () => {
  searchForm.name = ''
  searchForm.status = ''
  handleSearch()
}

const handleSizeChange = (val) => {
  pagination.pageSize = val
  loadServices()
}

const handleCurrentChange = (val) => {
  pagination.page = val
  loadServices()
}

const loadServices = async () => {
  loading.value = true
  try {
    const res = await getServiceList({
      page: pagination.page,
      pageSize: pagination.pageSize,
      ...searchForm
    })
    serviceList.value = res.list || []
    pagination.total = res.total || 0
  } catch (error) {
    console.error('加载服务列表失败', error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  dialogTitle.value = '添加服务'
  Object.assign(formData, {
    id: null,
    name: '',
    price: 0,
    description: '',
    duration: '',
    sort: 0,
    status: 1
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑服务'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleToggleStatus = (row) => {
  const action = row.status === 1 ? '下架' : '上架'
  ElMessageBox.confirm(`确定要${action}服务"${row.name}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const newStatus = row.status === 1 ? 0 : 1
    await updateService(row.id, { status: newStatus })
    row.status = newStatus
    ElMessage.success(`${action}成功`)
  }).catch(() => {})
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除服务"${row.name}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteService(row.id)
    ElMessage.success('删除成功')
    loadServices()
  }).catch(() => {})
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (formData.id) {
        await updateService(formData.id, formData)
        ElMessage.success('编辑成功')
      } else {
        await addService(formData)
        ElMessage.success('添加成功')
      }
      dialogVisible.value = false
      loadServices()
    }
  })
}

onMounted(() => {
  loadServices()
})
</script>

<style lang="scss" scoped>
.services-page {
  padding: 24px;
  background: #f1f5f9;
  min-height: 100vh;
}

// 页面头部
.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  padding: 32px;
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 10px 40px rgba(102, 126, 234, 0.3);
  animation: slideDown 0.6s ease-out;
  
  .header-content {
    .page-title {
      font-size: 32px;
      font-weight: 700;
      color: #ffffff;
      margin: 0 0 8px 0;
      text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }
    
    .page-subtitle {
      font-size: 16px;
      color: rgba(255, 255, 255, 0.9);
      margin: 0;
    }
  }
  
  .header-stats {
    display: flex;
    gap: 32px;
    
    .stat-item {
      text-align: center;
      padding: 16px 24px;
      background: rgba(255, 255, 255, 0.2);
      border-radius: 16px;
      backdrop-filter: blur(10px);
      
      .stat-value {
        display: block;
        font-size: 28px;
        font-weight: 700;
        color: #ffffff;
        margin-bottom: 4px;
      }
      
      .stat-label {
        font-size: 14px;
        color: rgba(255, 255, 255, 0.9);
      }
    }
  }
}

// 搜索区域
.search-section {
  margin-bottom: 24px;
  
  .search-form-wrapper {
    background: #ffffff;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 16px;
    animation: slideUp 0.6s ease-out;
    animation-delay: 0.1s;
    animation-fill-mode: both;
    
    .search-form {
      margin: 0;
      
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
    }
    
    .search-btn {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border: none;
      border-radius: 10px;
      padding: 10px 20px;
      font-weight: 500;
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
      }
    }
    
    .reset-btn {
      border-radius: 10px;
      padding: 10px 20px;
      font-weight: 500;
      transition: all 0.3s ease;
      
      &:hover {
        background: #f1f5f9;
        border-color: #e2e8f0;
      }
    }
    
    .add-btn {
      background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
      border: none;
      border-radius: 10px;
      padding: 12px 24px;
      font-weight: 500;
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 20px rgba(17, 153, 142, 0.4);
      }
    }
  }
}

// 表格区域
.table-section {
  animation: slideUp 0.6s ease-out;
  animation-delay: 0.2s;
  animation-fill-mode: both;
  
  .table-card {
    background: #ffffff;
    border-radius: 20px;
    padding: 24px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  }
}

// 服务信息单元格
.service-cell {
  display: flex;
  align-items: center;
  gap: 12px;
  
  .service-icon {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    background: linear-gradient(135deg, #e2e8f0 0%, #cbd5e1 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.3s ease;
    
    .el-icon {
      font-size: 24px;
      color: #64748b;
    }
    
    &.active {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      
      .el-icon {
        color: #ffffff;
      }
    }
  }
  
  .service-info {
    flex: 1;
    min-width: 0;
    
    .service-name {
      font-weight: 600;
      color: #1e293b;
      font-size: 15px;
      margin-bottom: 4px;
    }
    
    .service-desc {
      font-size: 13px;
      color: #64748b;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }
}

// 价格标签
.price-tag {
  display: inline-flex;
  align-items: baseline;
  gap: 2px;
  padding: 6px 12px;
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  border-radius: 20px;
  
  .price-symbol {
    font-size: 14px;
    color: #d97706;
    font-weight: 600;
  }
  
  .price-value {
    font-size: 18px;
    color: #b45309;
    font-weight: 700;
  }
}

// 时长信息
.duration-info {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #64748b;
  font-size: 14px;
  
  .el-icon {
    color: #94a3b8;
  }
}

// 统计信息
.stats-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  
  .sales-count {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 14px;
    color: #64748b;
    
    .el-icon {
      color: #f59e0b;
    }
  }
  
  .rating-stars {
    :deep(.el-rate__icon) {
      font-size: 14px;
    }
    
    :deep(.el-rate__text) {
      font-size: 12px;
      color: #f59e0b;
      font-weight: 600;
    }
  }
}

// 状态指示器
.status-wrapper {
  cursor: pointer;
  
  .status-indicator {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 6px 12px;
    border-radius: 20px;
    transition: all 0.3s ease;
    
    &.active {
      background: #dcfce7;
      
      .status-dot {
        background: #22c55e;
        box-shadow: 0 0 8px #22c55e;
      }
      
      .status-text {
        color: #15803d;
      }
    }
    
    &.inactive {
      background: #f1f5f9;
      
      .status-dot {
        background: #94a3b8;
      }
      
      .status-text {
        color: #64748b;
      }
    }
    
    &:hover {
      transform: scale(1.05);
    }
    
    .status-dot {
      width: 8px;
      height: 8px;
      border-radius: 50%;
      transition: all 0.3s ease;
    }
    
    .status-text {
      font-size: 13px;
      font-weight: 500;
    }
  }
}

// 操作按钮
.action-buttons {
  display: flex;
  justify-content: center;
  gap: 8px;
  
  .action-btn {
    width: 36px;
    height: 36px;
    transition: all 0.3s ease;
    
    &.edit {
      background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
      border: none;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
      }
    }
    
    &.toggle {
      &.el-button--warning {
        background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
        border: none;
      }
      
      &.el-button--success {
        background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
        border: none;
      }
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }
    }
    
    &.delete {
      background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
      border: none;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(239, 68, 68, 0.4);
      }
    }
    
    .el-icon {
      font-size: 16px;
    }
  }
}

// 分页
.pagination-wrapper {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #e2e8f0;
  display: flex;
  justify-content: flex-end;
  
  .pagination {
    :deep(.el-pagination__total) {
      color: #64748b;
    }
    
    :deep(.el-pagination__sizes) {
      .el-input__inner {
        border-radius: 8px;
      }
    }
    
    :deep(.el-pager li) {
      border-radius: 8px;
      
      &.active {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      }
    }
  }
}

// 对话框样式
.service-dialog {
  :deep(.el-dialog__header) {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 24px;
    margin: 0;
    
    .el-dialog__title {
      color: #ffffff;
      font-weight: 600;
      font-size: 18px;
    }
    
    .el-dialog__headerbtn .el-dialog__close {
      color: #ffffff;
      
      &:hover {
        color: rgba(255, 255, 255, 0.8);
      }
    }
  }
  
  :deep(.el-dialog__body) {
    padding: 32px;
  }
  
  .dialog-content {
    .service-form {
      .el-form-item {
        margin-bottom: 24px;
        
        .el-form-item__label {
          font-weight: 500;
          color: #475569;
        }
      }
      
      .form-input {
        :deep(.el-input__wrapper) {
          border-radius: 10px;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
        }
      }
      
      .form-number {
        :deep(.el-input__wrapper) {
          border-radius: 10px;
        }
      }
      
      .form-textarea {
        :deep(.el-textarea__inner) {
          border-radius: 10px;
          padding: 12px;
        }
      }
      
      .status-switch-wrapper {
        display: flex;
        align-items: center;
        gap: 16px;
        
        .status-switch {
          :deep(.el-switch__core) {
            border-radius: 20px;
          }
          
          :deep(.el-switch__label) {
            font-weight: 500;
          }
        }
        
        .switch-hint {
          font-size: 13px;
          color: #64748b;
        }
      }
    }
  }
  
  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    padding-top: 16px;
    border-top: 1px solid #e2e8f0;
    
    .cancel-btn {
      border-radius: 10px;
      padding: 10px 24px;
      
      &:hover {
        background: #f1f5f9;
      }
    }
    
    .submit-btn {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border: none;
      border-radius: 10px;
      padding: 10px 24px;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
      }
    }
  }
}

// 表格行悬停效果
:deep(.table-row) {
  transition: all 0.3s ease;
  
  &:hover {
    background: #f8fafc !important;
    transform: scale(1.002);
    
    .service-icon {
      transform: scale(1.1);
    }
  }
}

// 动画
@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .services-page {
    padding: 16px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 20px;
    text-align: center;
    
    .header-stats {
      width: 100%;
      justify-content: center;
    }
  }
  
  .search-form-wrapper {
    flex-direction: column;
    
    .search-form {
      width: 100%;
      
      .el-form-item {
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
