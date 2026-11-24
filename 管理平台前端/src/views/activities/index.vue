<template>
  <div class="activities-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">活动管理</h1>
        <p class="page-subtitle">管理平台所有社区活动</p>
      </div>
      <div class="header-stats">
        <div class="stat-item">
          <span class="stat-value">{{ stats.total }}</span>
          <span class="stat-label">活动总数</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ stats.participants }}</span>
          <span class="stat-label">参与人数</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ stats.likes }}</span>
          <span class="stat-label">总点赞</span>
        </div>
      </div>
    </div>
    
    <!-- 搜索区域 -->
    <div class="search-section">
      <div class="search-form-wrapper">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="活动名称">
            <el-input 
              v-model="searchForm.title" 
              placeholder="请输入活动名称" 
              clearable
              prefix-icon="Calendar"
              class="search-input"
            />
          </el-form-item>
          <el-form-item label="活动类型">
            <el-select v-model="searchForm.category" placeholder="请选择类型" clearable class="search-select">
              <el-option label="健康讲座" :value="1">
                <el-tag type="success" size="small" effect="light">健康讲座</el-tag>
              </el-option>
              <el-option label="文娱活动" :value="2">
                <el-tag type="primary" size="small" effect="light">文娱活动</el-tag>
              </el-option>
              <el-option label="志愿服务" :value="3">
                <el-tag type="warning" size="small" effect="light">志愿服务</el-tag>
              </el-option>
              <el-option label="兴趣社团" :value="4">
                <el-tag type="info" size="small" effect="light">兴趣社团</el-tag>
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
          <span>发布活动</span>
        </el-button>
      </div>
    </div>
    
    <!-- 数据表格 -->
    <div class="table-section">
      <div class="table-card">
        <el-table 
          :data="activityList" 
          v-loading="loading" 
          style="width: 100%"
          :header-cell-style="headerCellStyle"
          row-class-name="table-row"
        >
          <el-table-column type="index" label="#" width="60" align="center" />
          <el-table-column label="活动信息" min-width="280">
            <template #default="{ row }">
              <div class="activity-cell">
                <div class="activity-icon" :class="getCategoryClass(row.category)">
                  <el-icon><component :is="getCategoryIcon(row.category)" /></el-icon>
                </div>
                <div class="activity-info">
                  <div class="activity-title">{{ row.title }}</div>
                  <div class="activity-location">
                    <el-icon><Location /></el-icon>
                    <span>{{ row.location }}</span>
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="活动类型" width="120" align="center">
            <template #default="{ row }">
              <div class="category-tag" :class="getCategoryClass(row.category)">
                <el-icon><component :is="getCategoryIcon(row.category)" /></el-icon>
                <span>{{ getCategoryText(row.category) }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="活动时间" width="180" align="center">
            <template #default="{ row }">
              <div class="time-info">
                <div class="time-start">
                  <el-icon><Clock /></el-icon>
                  <span>{{ formatDateTime(row.startTime) }}</span>
                </div>
                <div class="time-end" v-if="row.endTime">
                  <span class="time-label">至</span>
                  <span>{{ formatDateTime(row.endTime) }}</span>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="参与情况" width="140" align="center">
            <template #default="{ row }">
              <div class="participants-info">
                <div class="participants-count">
                  <el-icon><User /></el-icon>
                  <span class="current">{{ row.currentParticipants || 0 }}</span>
                  <span class="divider">/</span>
                  <span class="max">{{ row.maxParticipants }}</span>
                </div>
                <div class="progress-bar">
                  <div 
                    class="progress-fill" 
                    :style="{ width: getProgressWidth(row) + '%' }"
                    :class="getProgressClass(row)"
                  ></div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="互动数据" width="120" align="center">
            <template #default="{ row }">
              <div class="interaction-stats">
                <div class="likes-count">
                  <el-icon><StarFilled /></el-icon>
                  <span>{{ row.likes || 0 }}</span>
                </div>
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
      width="700px"
      class="activity-dialog"
      destroy-on-close
    >
      <div class="dialog-content">
        <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px" class="activity-form">
          <el-form-item label="活动名称" prop="title">
            <el-input v-model="formData.title" placeholder="请输入活动名称" class="form-input">
              <template #prefix>
                <el-icon><Calendar /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="活动类型" prop="category">
            <el-select v-model="formData.category" placeholder="请选择活动类型" class="form-select">
              <el-option label="健康讲座" :value="1">
                <div class="category-option">
                  <el-icon color="#22c55e"><FirstAidKit /></el-icon>
                  <span>健康讲座</span>
                </div>
              </el-option>
              <el-option label="文娱活动" :value="2">
                <div class="category-option">
                  <el-icon color="#3b82f6"><Film /></el-icon>
                  <span>文娱活动</span>
                </div>
              </el-option>
              <el-option label="志愿服务" :value="3">
                <div class="category-option">
                  <el-icon color="#f59e0b"><Handbag /></el-icon>
                  <span>志愿服务</span>
                </div>
              </el-option>
              <el-option label="兴趣社团" :value="4">
                <div class="category-option">
                  <el-icon color="#64748b"><User /></el-icon>
                  <span>兴趣社团</span>
                </div>
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="活动地点" prop="location">
            <el-input v-model="formData.location" placeholder="请输入活动地点" class="form-input">
              <template #prefix>
                <el-icon><Location /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="开始时间" prop="startTime">
                <el-date-picker 
                  v-model="formData.startTime" 
                  type="datetime" 
                  placeholder="请选择开始时间" 
                  value-format="YYYY-MM-DD HH:mm:ss" 
                  class="form-date-picker"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="结束时间" prop="endTime">
                <el-date-picker 
                  v-model="formData.endTime" 
                  type="datetime" 
                  placeholder="请选择结束时间" 
                  value-format="YYYY-MM-DD HH:mm:ss" 
                  class="form-date-picker"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="最大人数" prop="maxParticipants">
            <el-input-number v-model="formData.maxParticipants" :min="1" class="form-number" />
          </el-form-item>
          <el-form-item label="联系电话" prop="contactPhone">
            <el-input v-model="formData.contactPhone" placeholder="请输入联系电话" class="form-input">
              <template #prefix>
                <el-icon><Phone /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="活动描述" prop="description">
            <el-input 
              v-model="formData.description" 
              type="textarea" 
              :rows="4" 
              placeholder="请输入活动描述"
              class="form-textarea"
              resize="none"
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getActivityList, addActivity, updateActivity, deleteActivity } from '@/api/activity'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('发布活动')
const formRef = ref(null)

const searchForm = reactive({
  title: '',
  category: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const activityList = ref([])

const formData = reactive({
  id: null,
  title: '',
  category: null,
  location: '',
  startTime: '',
  endTime: '',
  maxParticipants: 50,
  contactPhone: '',
  description: ''
})

const formRules = {
  title: [{ required: true, message: '请输入活动名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择活动类型', trigger: 'change' }],
  location: [{ required: true, message: '请输入活动地点', trigger: 'blur' }]
}

// 统计数据
const stats = computed(() => {
  return {
    total: activityList.value.length,
    participants: activityList.value.reduce((sum, item) => sum + (item.currentParticipants || 0), 0),
    likes: activityList.value.reduce((sum, item) => sum + (item.likes || 0), 0)
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

const getCategoryText = (category) => {
  const texts = { 1: '健康讲座', 2: '文娱活动', 3: '志愿服务', 4: '兴趣社团' }
  return texts[category] || '未知'
}

const getCategoryIcon = (category) => {
  const icons = { 1: 'FirstAidKit', 2: 'Film', 3: 'Handbag', 4: 'User' }
  return icons[category] || 'Calendar'
}

const getCategoryClass = (category) => {
  const classes = { 1: 'health', 2: 'entertainment', 3: 'volunteer', 4: 'club' }
  return classes[category] || ''
}

const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return datetime.replace('T', ' ').substring(0, 16)
}

const getProgressWidth = (row) => {
  if (!row.maxParticipants) return 0
  return Math.min(((row.currentParticipants || 0) / row.maxParticipants) * 100, 100)
}

const getProgressClass = (row) => {
  const percentage = getProgressWidth(row)
  if (percentage >= 80) return 'high'
  if (percentage >= 50) return 'medium'
  return 'low'
}

const handleSearch = () => {
  pagination.page = 1
  loadActivities()
}

const handleReset = () => {
  searchForm.title = ''
  searchForm.category = ''
  handleSearch()
}

const handleSizeChange = () => loadActivities()
const handleCurrentChange = () => loadActivities()

const loadActivities = async () => {
  loading.value = true
  try {
    const res = await getActivityList({
      page: pagination.page,
      pageSize: pagination.pageSize,
      ...searchForm
    })
    activityList.value = res.list || []
    pagination.total = res.total || 0
  } catch (error) {
    console.error('加载活动列表失败', error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  dialogTitle.value = '发布活动'
  Object.assign(formData, { id: null, title: '', category: null, location: '', startTime: '', endTime: '', maxParticipants: 50, contactPhone: '', description: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑活动'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除活动"${row.title}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteActivity(row.id)
    ElMessage.success('删除成功')
    loadActivities()
  }).catch(() => {})
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (formData.id) {
        await updateActivity(formData.id, formData)
        ElMessage.success('编辑成功')
      } else {
        await addActivity(formData)
        ElMessage.success('发布成功')
      }
      dialogVisible.value = false
      loadActivities()
    }
  })
}

onMounted(() => loadActivities())
</script>

<style lang="scss" scoped>
.activities-page {
  padding: 24px;
  background: #f1f5f9;
  min-height: 100vh;
}

// 页面头部
.page-header {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  border-radius: 20px;
  padding: 32px;
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 10px 40px rgba(240, 147, 251, 0.3);
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
      background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      border: none;
      border-radius: 10px;
      padding: 10px 20px;
      font-weight: 500;
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 20px rgba(240, 147, 251, 0.4);
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

// 活动信息单元格
.activity-cell {
  display: flex;
  align-items: center;
  gap: 12px;
  
  .activity-icon {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.3s ease;
    
    .el-icon {
      font-size: 24px;
      color: #ffffff;
    }
    
    &.health {
      background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
    }
    
    &.entertainment {
      background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
    }
    
    &.volunteer {
      background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
    }
    
    &.club {
      background: linear-gradient(135deg, #64748b 0%, #475569 100%);
    }
  }
  
  .activity-info {
    flex: 1;
    min-width: 0;
    
    .activity-title {
      font-weight: 600;
      color: #1e293b;
      font-size: 15px;
      margin-bottom: 4px;
    }
    
    .activity-location {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 13px;
      color: #64748b;
      
      .el-icon {
        font-size: 14px;
      }
    }
  }
}

// 类型标签
.category-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  
  &.health {
    background: #dcfce7;
    color: #15803d;
  }
  
  &.entertainment {
    background: #dbeafe;
    color: #1d4ed8;
  }
  
  &.volunteer {
    background: #fef3c7;
    color: #b45309;
  }
  
  &.club {
    background: #f1f5f9;
    color: #475569;
  }
}

// 时间信息
.time-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 13px;
  color: #64748b;
  
  .time-start {
    display: flex;
    align-items: center;
    gap: 4px;
    
    .el-icon {
      color: #f093fb;
    }
  }
  
  .time-end {
    display: flex;
    align-items: center;
    gap: 4px;
    padding-left: 20px;
    
    .time-label {
      color: #94a3b8;
      font-size: 12px;
    }
  }
}

// 参与情况
.participants-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  
  .participants-count {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 14px;
    
    .el-icon {
      color: #f093fb;
    }
    
    .current {
      font-weight: 700;
      color: #f5576c;
    }
    
    .divider {
      color: #94a3b8;
    }
    
    .max {
      color: #64748b;
    }
  }
  
  .progress-bar {
    width: 80px;
    height: 6px;
    background: #e2e8f0;
    border-radius: 3px;
    overflow: hidden;
    
    .progress-fill {
      height: 100%;
      border-radius: 3px;
      transition: width 0.3s ease;
      
      &.low {
        background: linear-gradient(90deg, #22c55e 0%, #16a34a 100%);
      }
      
      &.medium {
        background: linear-gradient(90deg, #f59e0b 0%, #d97706 100%);
      }
      
      &.high {
        background: linear-gradient(90deg, #ef4444 0%, #dc2626 100%);
      }
    }
  }
}

// 互动数据
.interaction-stats {
  .likes-count {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    padding: 8px 16px;
    background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
    border-radius: 20px;
    
    .el-icon {
      color: #f59e0b;
      font-size: 16px;
    }
    
    span {
      font-weight: 600;
      color: #b45309;
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
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      }
    }
  }
}

// 对话框样式
.activity-dialog {
  :deep(.el-dialog__header) {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
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
    .activity-form {
      .el-form-item {
        margin-bottom: 24px;
        
        .el-form-item__label {
          font-weight: 500;
          color: #475569;
        }
      }
      
      .form-input,
      .form-select,
      .form-date-picker {
        width: 100%;
        
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
      
      .category-option {
        display: flex;
        align-items: center;
        gap: 8px;
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
      background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      border: none;
      border-radius: 10px;
      padding: 10px 24px;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 20px rgba(240, 147, 251, 0.4);
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
    
    .activity-icon {
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
  .activities-page {
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
