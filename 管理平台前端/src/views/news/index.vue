<template>
  <div class="news-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">新闻管理</h1>
        <p class="page-subtitle">管理平台所有资讯内容</p>
      </div>
      <div class="header-stats">
        <div class="stat-item">
          <span class="stat-value">{{ stats.total }}</span>
          <span class="stat-label">资讯总数</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ stats.published }}</span>
          <span class="stat-label">已发布</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ stats.views }}</span>
          <span class="stat-label">总浏览</span>
        </div>
      </div>
    </div>
    
    <!-- 搜索区域 -->
    <div class="search-section">
      <div class="search-form-wrapper">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="标题">
            <el-input 
              v-model="searchForm.title" 
              placeholder="请输入标题" 
              clearable
              prefix-icon="Document"
              class="search-input"
            />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable class="search-select">
              <el-option label="已发布" :value="1">
                <el-tag type="success" size="small" effect="light">已发布</el-tag>
              </el-option>
              <el-option label="草稿" :value="0">
                <el-tag type="info" size="small" effect="light">草稿</el-tag>
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
          <span>发布资讯</span>
        </el-button>
      </div>
    </div>
    
    <!-- 数据表格 -->
    <div class="table-section">
      <div class="table-card">
        <el-table 
          :data="newsList" 
          v-loading="loading" 
          style="width: 100%"
          :header-cell-style="headerCellStyle"
          row-class-name="table-row"
        >
          <el-table-column type="index" label="#" width="60" align="center" />
          <el-table-column label="资讯标题" min-width="300">
            <template #default="{ row }">
              <div class="news-cell">
                <div class="news-icon" :class="row.status === 1 ? 'published' : 'draft'">
                  <el-icon><Document /></el-icon>
                </div>
                <div class="news-info">
                  <div class="news-title">{{ row.title }}</div>
                  <div class="news-summary" v-if="row.summary">{{ row.summary }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="作者" width="120" align="center">
            <template #default="{ row }">
              <div class="author-info">
                <el-icon><User /></el-icon>
                <span>{{ row.author || '匿名' }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="浏览量" width="120" align="center">
            <template #default="{ row }">
              <div class="view-count">
                <el-icon><View /></el-icon>
                <span>{{ formatNumber(row.viewCount || 0) }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">
              <div class="status-wrapper">
                <div class="status-badge" :class="row.status === 1 ? 'published' : 'draft'">
                  <div class="status-dot"></div>
                  <span class="status-text">{{ row.status === 1 ? '已发布' : '草稿' }}</span>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="发布时间" width="160" align="center">
            <template #default="{ row }">
              <div class="time-info">
                <el-icon><Clock /></el-icon>
                <span>{{ formatDateTime(row.createTime) }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right" align="center">
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
            @size-change="loadNews"
            @current-change="loadNews"
          />
        </div>
      </div>
    </div>
    
    <!-- 添加/编辑对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle" 
      width="800px"
      class="news-dialog"
      destroy-on-close
    >
      <div class="dialog-content">
        <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px" class="news-form">
          <el-form-item label="标题" prop="title">
            <el-input v-model="formData.title" placeholder="请输入资讯标题" class="form-input">
              <template #prefix>
                <el-icon><Document /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="作者" prop="author">
            <el-input v-model="formData.author" placeholder="请输入作者姓名" class="form-input">
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="摘要" prop="summary">
            <el-input 
              v-model="formData.summary" 
              type="textarea" 
              :rows="2" 
              placeholder="请输入资讯摘要（可选）"
              class="form-textarea"
              resize="none"
            />
          </el-form-item>
          <el-form-item label="内容" prop="content">
            <el-input 
              v-model="formData.content" 
              type="textarea" 
              :rows="8" 
              placeholder="请输入资讯内容"
              class="form-textarea content-textarea"
              resize="none"
            />
          </el-form-item>
          <el-form-item label="发布状态">
            <div class="status-radio-wrapper">
              <el-radio-group v-model="formData.status" class="status-radio-group">
                <el-radio :label="1" class="status-radio">
                  <div class="radio-content">
                    <el-icon color="#22c55e"><CircleCheck /></el-icon>
                    <span>立即发布</span>
                  </div>
                </el-radio>
                <el-radio :label="0" class="status-radio">
                  <div class="radio-content">
                    <el-icon color="#94a3b8"><Document /></el-icon>
                    <span>保存草稿</span>
                  </div>
                </el-radio>
              </el-radio-group>
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
import { getNewsList, addNews, updateNews, deleteNews } from '@/api/news'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('发布资讯')
const formRef = ref(null)

const searchForm = reactive({ title: '', status: '' })
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const newsList = ref([])

const formData = reactive({ id: null, title: '', author: '', summary: '', content: '', status: 1 })
const formRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

// 统计数据
const stats = computed(() => {
  return {
    total: newsList.value.length,
    published: newsList.value.filter(item => item.status === 1).length,
    views: newsList.value.reduce((sum, item) => sum + (item.viewCount || 0), 0)
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

const formatNumber = (num) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  }
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}

const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return datetime.replace('T', ' ').substring(0, 16)
}

const handleSearch = () => { pagination.page = 1; loadNews() }
const handleReset = () => { searchForm.title = ''; searchForm.status = ''; handleSearch() }

const loadNews = async () => {
  loading.value = true
  try {
    const res = await getNewsList({ page: pagination.page, pageSize: pagination.pageSize, ...searchForm })
    newsList.value = res.list || []
    pagination.total = res.total || 0
  } catch (error) {
    console.error('加载资讯列表失败', error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  dialogTitle.value = '发布资讯'
  Object.assign(formData, { id: null, title: '', author: '', summary: '', content: '', status: 1 })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑资讯'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除资讯"${row.title}"吗？`, '提示', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
  }).then(async () => {
    await deleteNews(row.id)
    ElMessage.success('删除成功')
    loadNews()
  }).catch(() => {})
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (formData.id) {
        await updateNews(formData.id, formData)
        ElMessage.success('编辑成功')
      } else {
        await addNews(formData)
        ElMessage.success('发布成功')
      }
      dialogVisible.value = false
      loadNews()
    }
  })
}

onMounted(() => loadNews())
</script>

<style lang="scss" scoped>
.news-page {
  padding: 24px;
  background: #f1f5f9;
  min-height: 100vh;
}

// 页面头部
.page-header {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  border-radius: 20px;
  padding: 32px;
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 10px 40px rgba(79, 172, 254, 0.3);
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
      background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      border: none;
      border-radius: 10px;
      padding: 10px 20px;
      font-weight: 500;
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 20px rgba(79, 172, 254, 0.4);
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

// 资讯信息单元格
.news-cell {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  
  .news-icon {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    transition: all 0.3s ease;
    
    .el-icon {
      font-size: 24px;
      color: #ffffff;
    }
    
    &.published {
      background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    }
    
    &.draft {
      background: linear-gradient(135deg, #94a3b8 0%, #64748b 100%);
    }
  }
  
  .news-info {
    flex: 1;
    min-width: 0;
    
    .news-title {
      font-weight: 600;
      color: #1e293b;
      font-size: 15px;
      margin-bottom: 4px;
      line-height: 1.4;
    }
    
    .news-summary {
      font-size: 13px;
      color: #64748b;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }
}

// 作者信息
.author-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  color: #64748b;
  font-size: 14px;
  
  .el-icon {
    color: #4facfe;
  }
}

// 浏览量
.view-count {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: linear-gradient(135deg, #e0e7ff 0%, #c7d2fe 100%);
  border-radius: 20px;
  
  .el-icon {
    color: #6366f1;
  }
  
  span {
    font-weight: 600;
    color: #4f46e5;
  }
}

// 状态徽章
.status-wrapper {
  .status-badge {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 6px 12px;
    border-radius: 20px;
    transition: all 0.3s ease;
    
    &.published {
      background: #dcfce7;
      
      .status-dot {
        background: #22c55e;
        box-shadow: 0 0 8px #22c55e;
      }
      
      .status-text {
        color: #15803d;
      }
    }
    
    &.draft {
      background: #f1f5f9;
      
      .status-dot {
        background: #94a3b8;
      }
      
      .status-text {
        color: #64748b;
      }
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

// 时间信息
.time-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 13px;
  color: #64748b;
  
  .el-icon {
    color: #4facfe;
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
        background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      }
    }
  }
}

// 对话框样式
.news-dialog {
  :deep(.el-dialog__header) {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
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
    .news-form {
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
      
      .form-textarea {
        :deep(.el-textarea__inner) {
          border-radius: 10px;
          padding: 12px;
        }
        
        &.content-textarea {
          :deep(.el-textarea__inner) {
            font-family: 'Monaco', 'Menlo', 'Consolas', monospace;
            font-size: 14px;
            line-height: 1.6;
          }
        }
      }
      
      .status-radio-wrapper {
        .status-radio-group {
          display: flex;
          gap: 24px;
          
          .status-radio {
            margin-right: 0;
            
            :deep(.el-radio__input) {
              display: none;
            }
            
            :deep(.el-radio__label) {
              padding: 0;
            }
            
            .radio-content {
              display: flex;
              align-items: center;
              gap: 8px;
              padding: 12px 20px;
              border-radius: 12px;
              border: 2px solid #e2e8f0;
              transition: all 0.3s ease;
              cursor: pointer;
              
              .el-icon {
                font-size: 20px;
              }
              
              span {
                font-weight: 500;
                color: #475569;
              }
            }
            
            &.is-checked {
              .radio-content {
                border-color: #4facfe;
                background: linear-gradient(135deg, rgba(79, 172, 254, 0.1) 0%, rgba(0, 242, 254, 0.1) 100%);
                
                span {
                  color: #4facfe;
                  font-weight: 600;
                }
              }
            }
            
            &:hover:not(.is-checked) {
              .radio-content {
                border-color: #cbd5e1;
                background: #f8fafc;
              }
            }
          }
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
      background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      border: none;
      border-radius: 10px;
      padding: 10px 24px;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 20px rgba(79, 172, 254, 0.4);
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
    
    .news-icon {
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
  .news-page {
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
