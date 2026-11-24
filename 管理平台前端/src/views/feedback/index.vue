<template>
  <div class="feedback-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">反馈管理</h1>
        <p class="page-subtitle">管理用户意见反馈与投诉</p>
      </div>
      <div class="header-stats">
        <div class="stat-item">
          <span class="stat-value">{{ stats.total }}</span>
          <span class="stat-label">反馈总数</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ stats.pending }}</span>
          <span class="stat-label">待处理</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ stats.resolved }}</span>
          <span class="stat-label">已处理</span>
        </div>
      </div>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <div class="search-form-wrapper">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="处理状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable class="search-select">
              <el-option label="待处理" :value="0">
                <el-tag type="warning" size="small" effect="light">待处理</el-tag>
              </el-option>
              <el-option label="已处理" :value="1">
                <el-tag type="success" size="small" effect="light">已处理</el-tag>
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
          :data="feedbackList" 
          v-loading="loading" 
          style="width: 100%"
          :header-cell-style="headerCellStyle"
          row-class-name="table-row"
        >
          <el-table-column type="index" label="#" width="60" align="center" />
          <el-table-column label="用户信息" min-width="180">
            <template #default="{ row }">
              <div class="user-cell">
                <el-avatar :size="36" :src="row.userAvatar" class="user-avatar">
                  {{ row.userName?.charAt(0) || 'U' }}
                </el-avatar>
                <div class="user-info">
                  <div class="user-name">{{ row.userName || '用户' + row.userId }}</div>
                  <div class="user-id">ID: {{ row.userId }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="反馈内容" min-width="300">
            <template #default="{ row }">
              <div class="feedback-content">
                <div class="content-text">{{ row.content }}</div>
                <div class="content-preview" v-if="row.content.length > 50">
                  {{ row.content.substring(0, 50) }}...
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="联系方式" width="140" align="center">
            <template #default="{ row }">
              <div class="contact-info">
                <el-icon><Phone /></el-icon>
                <span>{{ row.contact || '-' }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">
              <div class="status-badge" :class="row.status === 1 ? 'resolved' : 'pending'">
                <div class="status-dot"></div>
                <span class="status-text">{{ row.status === 1 ? '已处理' : '待处理' }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="提交时间" width="160" align="center">
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
                <el-tooltip content="查看详情" placement="top">
                  <el-button 
                    type="primary" 
                    circle
                    class="action-btn view"
                    @click="handleView(row)"
                  >
                    <el-icon><View /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip v-if="row.status === 0" content="回复" placement="top">
                  <el-button 
                    type="success" 
                    circle
                    class="action-btn reply"
                    @click="handleReply(row)"
                  >
                    <el-icon><ChatDotRound /></el-icon>
                  </el-button>
                </el-tooltip>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination-wrapper">
          <el-pagination 
            v-model:current-page="pagination.page" 
            v-model:page-size="pagination.pageSize" 
            :page-sizes="[10, 20, 50, 100]"
            :total="pagination.total" 
            layout="total, sizes, prev, pager, next, jumper" 
            class="pagination" 
            @size-change="loadFeedback" 
            @current-change="loadFeedback" 
          />
        </div>
      </div>
    </div>

    <!-- 反馈详情对话框 -->
    <el-dialog 
      v-model="detailVisible" 
      title="反馈详情" 
      width="600px"
      class="detail-dialog"
      destroy-on-close
    >
      <div class="detail-content">
        <div class="detail-header">
          <div class="user-info-large">
            <el-avatar :size="48" :src="feedbackDetail.userAvatar" class="user-avatar-large">
              {{ feedbackDetail.userName?.charAt(0) || 'U' }}
            </el-avatar>
            <div class="user-meta">
              <div class="user-name-large">{{ feedbackDetail.userName || '用户' + feedbackDetail.userId }}</div>
              <div class="submit-time">
                <el-icon><Clock /></el-icon>
                <span>{{ formatDateTime(feedbackDetail.createTime) }}</span>
              </div>
            </div>
          </div>
          <div class="status-tag" :class="feedbackDetail.status === 1 ? 'resolved' : 'pending'">
            {{ feedbackDetail.status === 1 ? '已处理' : '待处理' }}
          </div>
        </div>
        
        <div class="detail-section">
          <div class="section-title">
            <el-icon><ChatSquare /></el-icon>
            <span>反馈内容</span>
          </div>
          <div class="section-content">{{ feedbackDetail.content }}</div>
        </div>
        
        <div class="detail-section" v-if="feedbackDetail.contact">
          <div class="section-title">
            <el-icon><Phone /></el-icon>
            <span>联系方式</span>
          </div>
          <div class="section-content contact">{{ feedbackDetail.contact }}</div>
        </div>
        
        <div class="detail-section reply-section" v-if="feedbackDetail.reply">
          <div class="section-title">
            <el-icon><ChatDotRound /></el-icon>
            <span>回复内容</span>
          </div>
          <div class="section-content reply">{{ feedbackDetail.reply }}</div>
        </div>
      </div>
    </el-dialog>

    <!-- 回复对话框 -->
    <el-dialog 
      v-model="replyVisible" 
      title="回复反馈" 
      width="500px"
      class="reply-dialog"
      destroy-on-close
    >
      <div class="reply-content">
        <div class="original-feedback">
          <div class="feedback-label">原始反馈</div>
          <div class="feedback-text">{{ replyForm.originalContent }}</div>
        </div>
        <el-form :model="replyForm" label-width="0" class="reply-form">
          <el-form-item>
            <div class="reply-label">回复内容</div>
            <el-input 
              v-model="replyForm.reply" 
              type="textarea" 
              :rows="5" 
              placeholder="请输入回复内容，将发送给用户"
              class="reply-textarea"
              resize="none"
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button class="cancel-btn" @click="replyVisible = false">取消</el-button>
          <el-button type="primary" class="submit-btn" @click="handleReplySubmit">
            <el-icon><Position /></el-icon>
            <span>发送回复</span>
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getFeedbackList, replyFeedback } from '@/api/feedback'

const loading = ref(false)
const detailVisible = ref(false)
const replyVisible = ref(false)
const searchForm = reactive({ status: '' })
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const feedbackList = ref([])
const feedbackDetail = ref({})
const replyForm = reactive({ id: null, reply: '', originalContent: '' })

// 统计数据
const stats = computed(() => {
  return {
    total: feedbackList.value.length,
    pending: feedbackList.value.filter(item => item.status === 0).length,
    resolved: feedbackList.value.filter(item => item.status === 1).length
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

const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return datetime.replace('T', ' ').substring(0, 16)
}

const handleSearch = () => { pagination.page = 1; loadFeedback() }
const handleReset = () => { searchForm.status = ''; handleSearch() }

const loadFeedback = async () => {
  loading.value = true
  try {
    const res = await getFeedbackList({ page: pagination.page, pageSize: pagination.pageSize, ...searchForm })
    feedbackList.value = res.list || []
    pagination.total = res.total || 0
  } catch (error) {
    console.error('加载反馈列表失败', error)
  } finally {
    loading.value = false
  }
}

const handleView = (row) => { 
  feedbackDetail.value = { ...row }; 
  detailVisible.value = true 
}

const handleReply = (row) => { 
  replyForm.id = row.id; 
  replyForm.reply = ''; 
  replyForm.originalContent = row.content;
  replyVisible.value = true 
}

const handleReplySubmit = async () => {
  if (!replyForm.reply) { ElMessage.warning('请输入回复内容'); return }
  await replyFeedback(replyForm.id, replyForm.reply)
  ElMessage.success('回复成功')
  replyVisible.value = false
  loadFeedback()
}

onMounted(() => loadFeedback())
</script>

<style lang="scss" scoped>
.feedback-page {
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

// 用户信息单元格
.user-cell {
  display: flex;
  align-items: center;
  gap: 12px;
  
  .user-avatar {
    flex-shrink: 0;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #ffffff;
    font-weight: 600;
  }
  
  .user-info {
    .user-name {
      font-weight: 600;
      color: #1e293b;
      font-size: 14px;
      margin-bottom: 2px;
    }
    
    .user-id {
      font-size: 12px;
      color: #94a3b8;
    }
  }
}

// 反馈内容
.feedback-content {
  .content-text {
    color: #1e293b;
    font-size: 14px;
    line-height: 1.5;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
  
  .content-preview {
    font-size: 12px;
    color: #94a3b8;
    margin-top: 4px;
  }
}

// 联系方式
.contact-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  color: #64748b;
  font-size: 14px;
  
  .el-icon {
    color: #667eea;
  }
}

// 状态徽章
.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 20px;
  transition: all 0.3s ease;
  
  &.resolved {
    background: #dcfce7;
    
    .status-dot {
      background: #22c55e;
      box-shadow: 0 0 8px #22c55e;
    }
    
    .status-text {
      color: #15803d;
    }
  }
  
  &.pending {
    background: #fef3c7;
    
    .status-dot {
      background: #f59e0b;
      box-shadow: 0 0 8px #f59e0b;
    }
    
    .status-text {
      color: #b45309;
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

// 时间信息
.time-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 13px;
  color: #64748b;
  
  .el-icon {
    color: #667eea;
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
    
    &.view {
      background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
      border: none;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
      }
    }
    
    &.reply {
      background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
      border: none;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(34, 197, 94, 0.4);
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

// 详情对话框
.detail-dialog {
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
    padding: 0;
  }
  
  .detail-content {
    padding: 24px;
    
    .detail-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding-bottom: 20px;
      border-bottom: 1px solid #e2e8f0;
      margin-bottom: 20px;
      
      .user-info-large {
        display: flex;
        align-items: center;
        gap: 16px;
        
        .user-avatar-large {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          color: #ffffff;
          font-weight: 600;
        }
        
        .user-meta {
          .user-name-large {
            font-size: 18px;
            font-weight: 600;
            color: #1e293b;
            margin-bottom: 4px;
          }
          
          .submit-time {
            display: flex;
            align-items: center;
            gap: 6px;
            font-size: 13px;
            color: #94a3b8;
          }
        }
      }
      
      .status-tag {
        padding: 8px 16px;
        border-radius: 20px;
        font-size: 14px;
        font-weight: 500;
        
        &.resolved {
          background: #dcfce7;
          color: #15803d;
        }
        
        &.pending {
          background: #fef3c7;
          color: #b45309;
        }
      }
    }
    
    .detail-section {
      margin-bottom: 20px;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      &.reply-section {
        background: #f0fdf4;
        padding: 16px;
        border-radius: 12px;
        border-left: 4px solid #22c55e;
      }
      
      .section-title {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 14px;
        font-weight: 600;
        color: #475569;
        margin-bottom: 8px;
        
        .el-icon {
          color: #667eea;
        }
      }
      
      .section-content {
        font-size: 14px;
        color: #1e293b;
        line-height: 1.6;
        padding-left: 28px;
        
        &.contact {
          color: #667eea;
          font-weight: 500;
        }
        
        &.reply {
          color: #15803d;
        }
      }
    }
  }
}

// 回复对话框
.reply-dialog {
  :deep(.el-dialog__header) {
    background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
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
    padding: 24px;
  }
  
  .reply-content {
    .original-feedback {
      background: #f8fafc;
      padding: 16px;
      border-radius: 12px;
      margin-bottom: 20px;
      
      .feedback-label {
        font-size: 12px;
        font-weight: 600;
        color: #94a3b8;
        text-transform: uppercase;
        margin-bottom: 8px;
        letter-spacing: 0.5px;
      }
      
      .feedback-text {
        font-size: 14px;
        color: #475569;
        line-height: 1.5;
      }
    }
    
    .reply-form {
      .reply-label {
        font-size: 14px;
        font-weight: 600;
        color: #475569;
        margin-bottom: 8px;
      }
      
      .reply-textarea {
        :deep(.el-textarea__inner) {
          border-radius: 12px;
          padding: 12px;
          font-size: 14px;
          line-height: 1.6;
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
      background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
      border: none;
      border-radius: 10px;
      padding: 10px 24px;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 20px rgba(34, 197, 94, 0.4);
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
  .feedback-page {
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
        
        .search-select {
          width: 100%;
        }
      }
    }
  }
}
</style>
