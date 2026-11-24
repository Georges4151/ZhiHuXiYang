<template>
  <div class="emergency-page">
    <!-- 紧急警报横幅 -->
    <div class="emergency-banner" v-if="stats.pending > 0">
      <div class="banner-content">
        <div class="alert-icon">
          <el-icon :size="32"><WarningFilled /></el-icon>
        </div>
        <div class="alert-text">
          <span class="alert-title">紧急警报</span>
          <span class="alert-message">当前有 {{ stats.pending }} 个待处理的紧急呼叫，请立即处理！</span>
        </div>
        <el-button type="danger" class="alert-btn" @click="scrollToTable">
          <el-icon><View /></el-icon>
          <span>查看详情</span>
        </el-button>
      </div>
    </div>

    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">紧急求助</h1>
        <p class="page-subtitle">管理用户紧急呼叫与求助请求</p>
      </div>
      <div class="header-icon">
        <el-icon :size="48"><PhoneFilled /></el-icon>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="8">
          <div class="stat-card danger" style="animation-delay: 0s;">
            <div class="stat-bg"></div>
            <div class="stat-content">
              <div class="stat-icon-wrapper danger">
                <el-icon :size="28"><PhoneFilled /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.pending }}</div>
                <div class="stat-label">待处理呼叫</div>
              </div>
              <div class="stat-trend" v-if="stats.pending > 0">
                <el-icon class="trend-icon"><WarningFilled /></el-icon>
                <span>紧急</span>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="8">
          <div class="stat-card warning" style="animation-delay: 0.1s;">
            <div class="stat-bg"></div>
            <div class="stat-content">
              <div class="stat-icon-wrapper warning">
                <el-icon :size="28"><Loading /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.processing }}</div>
                <div class="stat-label">处理中</div>
              </div>
              <div class="stat-trend" v-if="stats.processing > 0">
                <el-icon class="trend-icon"><Timer /></el-icon>
                <span>进行中</span>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="8">
          <div class="stat-card success" style="animation-delay: 0.2s;">
            <div class="stat-bg"></div>
            <div class="stat-content">
              <div class="stat-icon-wrapper success">
                <el-icon :size="28"><CircleCheck /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.completed }}</div>
                <div class="stat-label">已完成</div>
              </div>
              <div class="stat-trend">
                <el-icon class="trend-icon"><TrendCharts /></el-icon>
                <span>今日</span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <div class="search-form-wrapper">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="用户姓名">
            <el-input 
              v-model="searchForm.userName" 
              placeholder="请输入用户姓名" 
              clearable 
              prefix-icon="User"
              class="search-input"
            />
          </el-form-item>
          <el-form-item label="处理状态">
            <el-select v-model="searchForm.callStatus" placeholder="请选择状态" clearable class="search-select">
              <el-option label="待处理" :value="0">
                <el-tag type="danger" size="small" effect="dark">待处理</el-tag>
              </el-option>
              <el-option label="处理中" :value="1">
                <el-tag type="warning" size="small" effect="dark">处理中</el-tag>
              </el-option>
              <el-option label="已完成" :value="2">
                <el-tag type="success" size="small" effect="dark">已完成</el-tag>
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
    <div class="table-section" ref="tableRef">
      <div class="table-card">
        <div class="card-header">
          <div class="header-left">
            <div class="header-icon-small danger">
              <el-icon><PhoneFilled /></el-icon>
            </div>
            <div class="header-text">
              <h3>紧急呼叫记录</h3>
              <p>实时显示用户紧急求助请求</p>
            </div>
          </div>
          <div class="header-right">
            <el-tag type="danger" effect="dark" class="live-tag">
              <el-icon><VideoPlay /></el-icon>
              <span>实时监控</span>
            </el-tag>
          </div>
        </div>
        <el-table 
          :data="emergencyList" 
          v-loading="loading" 
          style="width: 100%"
          :header-cell-style="headerCellStyle"
          row-class-name="table-row"
          :row-class-name="getRowClass"
        >
          <el-table-column type="index" label="#" width="60" align="center" />
          <el-table-column label="用户信息" min-width="180">
            <template #default="{ row }">
              <div class="user-cell">
                <el-avatar :size="40" class="user-avatar emergency">
                  {{ row.userName?.charAt(0) || 'U' }}
                </el-avatar>
                <div class="user-info">
                  <div class="user-name">{{ row.userName || '未知用户' }}</div>
                  <div class="user-phone">
                    <el-icon><Phone /></el-icon>
                    <span>{{ row.userPhone || '-' }}</span>
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="位置信息" min-width="280">
            <template #default="{ row }">
              <div class="location-info">
                <div class="location-icon">
                  <el-icon><Location /></el-icon>
                </div>
                <div class="location-text">{{ row.address || '位置未知' }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="呼叫时间" width="160" align="center">
            <template #default="{ row }">
              <div class="time-info" :class="{ 'urgent': isUrgent(row.createTime) }">
                <el-icon><Clock /></el-icon>
                <span>{{ formatDateTime(row.createTime) }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="120" align="center">
            <template #default="{ row }">
              <div class="status-badge" :class="getStatusClass(row.callStatus)">
                <div class="status-pulse" v-if="row.callStatus === 0"></div>
                <el-icon v-if="row.callStatus === 0" class="status-icon"><WarningFilled /></el-icon>
                <el-icon v-else-if="row.callStatus === 1" class="status-icon"><Loading /></el-icon>
                <el-icon v-else class="status-icon"><CircleCheck /></el-icon>
                <span class="status-text">{{ getStatusText(row.callStatus) }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right" align="center">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-tooltip v-if="row.callStatus === 0" content="开始处理" placement="top">
                  <el-button 
                    type="success" 
                    circle
                    class="action-btn process"
                    @click="handleProcess(row)"
                  >
                    <el-icon><VideoPlay /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip v-if="row.callStatus === 1" content="标记完成" placement="top">
                  <el-button 
                    type="primary" 
                    circle
                    class="action-btn complete"
                    @click="handleComplete(row)"
                  >
                    <el-icon><CircleCheck /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip content="查看位置" placement="top">
                  <el-button 
                    type="info" 
                    circle
                    class="action-btn location"
                    @click="handleViewLocation(row)"
                  >
                    <el-icon><MapLocation /></el-icon>
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
            @size-change="loadEmergency" 
            @current-change="loadEmergency" 
          />
        </div>
      </div>
    </div>

    <!-- 位置查看对话框 -->
    <el-dialog 
      v-model="locationVisible" 
      title="用户位置" 
      width="700px"
      class="location-dialog"
      destroy-on-close
    >
      <div class="location-content">
        <div class="location-header">
          <div class="user-info-large">
            <el-avatar :size="48" class="user-avatar-large emergency">
              {{ currentLocation.userName?.charAt(0) || 'U' }}
            </el-avatar>
            <div class="user-meta">
              <div class="user-name-large">{{ currentLocation.userName || '未知用户' }}</div>
              <div class="user-phone-large">
                <el-icon><Phone /></el-icon>
                <span>{{ currentLocation.userPhone || '-' }}</span>
              </div>
            </div>
          </div>
          <div class="call-time">
            <el-icon><Clock /></el-icon>
            <span>{{ formatDateTime(currentLocation.createTime) }}</span>
          </div>
        </div>
        <div class="location-map">
          <div class="map-placeholder">
            <el-icon :size="64"><MapLocation /></el-icon>
            <p>地图加载中...</p>
            <p class="location-address">{{ currentLocation.address }}</p>
          </div>
        </div>
        <div class="location-actions">
          <el-button type="primary" class="action-btn-navigate">
            <el-icon><Position /></el-icon>
            <span>导航到该位置</span>
          </el-button>
          <el-button type="success" class="action-btn-call">
            <el-icon><Phone /></el-icon>
            <span>联系用户</span>
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getEmergencyList, updateEmergencyStatus, getEmergencyStats } from '@/api/emergency'

const loading = ref(false)
const locationVisible = ref(false)
const tableRef = ref(null)
const searchForm = reactive({ userName: '', callStatus: '' })
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const emergencyList = ref([])
const stats = ref({ pending: 0, processing: 0, completed: 0 })
const currentLocation = ref({})

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

const getStatusClass = (status) => {
  return { 0: 'danger', 1: 'warning', 2: 'success' }[status] || 'info'
}

const getStatusText = (status) => {
  return { 0: '待处理', 1: '处理中', 2: '已完成' }[status] || '未知'
}

const getRowClass = ({ row }) => {
  if (row.callStatus === 0) return 'urgent-row'
  return ''
}

const isUrgent = (time) => {
  if (!time) return false
  const callTime = new Date(time).getTime()
  const now = Date.now()
  return (now - callTime) > 5 * 60 * 1000 // 超过5分钟
}

const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return datetime.replace('T', ' ').substring(0, 16)
}

const scrollToTable = () => {
  tableRef.value?.scrollIntoView({ behavior: 'smooth' })
}

const handleSearch = () => { pagination.page = 1; loadEmergency() }
const handleReset = () => { searchForm.userName = ''; searchForm.callStatus = ''; handleSearch() }

const loadEmergency = async () => {
  loading.value = true
  try {
    const res = await getEmergencyList({ page: pagination.page, pageSize: pagination.pageSize, ...searchForm })
    emergencyList.value = res.list || []
    pagination.total = res.total || 0
  } catch (error) {
    console.error('加载紧急呼叫失败', error)
  } finally {
    loading.value = false
  }
}

const loadStats = async () => {
  try { stats.value = await getEmergencyStats() } catch (error) { console.error('加载统计数据失败', error) }
}

const handleProcess = async (row) => {
  await updateEmergencyStatus(row.id, 1)
  row.callStatus = 1
  ElMessage.success('已开始处理')
  loadStats()
}

const handleComplete = async (row) => {
  await updateEmergencyStatus(row.id, 2)
  row.callStatus = 2
  ElMessage.success('处理完成')
  loadStats()
}

const handleViewLocation = (row) => {
  currentLocation.value = { ...row }
  locationVisible.value = true
}

onMounted(() => { loadEmergency(); loadStats() })
</script>

<style lang="scss" scoped>
.emergency-page {
  padding: 24px;
  background: #f1f5f9;
  min-height: 100vh;
}

// 紧急警报横幅
.emergency-banner {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  border-radius: 16px;
  padding: 20px 24px;
  margin-bottom: 24px;
  animation: pulse 2s ease-in-out infinite, slideDown 0.6s ease-out;
  box-shadow: 0 10px 40px rgba(239, 68, 68, 0.4);
  
  .banner-content {
    display: flex;
    align-items: center;
    gap: 16px;
    
    .alert-icon {
      width: 56px;
      height: 56px;
      background: rgba(255, 255, 255, 0.2);
      border-radius: 16px;
      display: flex;
      align-items: center;
      justify-content: center;
      animation: shake 0.5s ease-in-out infinite;
      
      .el-icon {
        color: #ffffff;
      }
    }
    
    .alert-text {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 4px;
      
      .alert-title {
        font-size: 18px;
        font-weight: 700;
        color: #ffffff;
      }
      
      .alert-message {
        font-size: 14px;
        color: rgba(255, 255, 255, 0.9);
      }
    }
    
    .alert-btn {
      background: #ffffff;
      color: #ef4444;
      border: none;
      border-radius: 10px;
      padding: 12px 24px;
      font-weight: 600;
      
      &:hover {
        background: rgba(255, 255, 255, 0.9);
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
      }
    }
  }
}

// 页面头部
.page-header {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  border-radius: 20px;
  padding: 32px;
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 10px 40px rgba(239, 68, 68, 0.3);
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
  
  .header-icon {
    width: 80px;
    height: 80px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    backdrop-filter: blur(10px);
    
    .el-icon {
      color: #ffffff;
    }
  }
}

// 统计区域
.stats-section {
  margin-bottom: 24px;
  
  .stat-card {
    background: #ffffff;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
    position: relative;
    overflow: hidden;
    animation: slideUp 0.6s ease-out;
    animation-fill-mode: both;
    
    .stat-bg {
      position: absolute;
      top: 0;
      right: 0;
      width: 120px;
      height: 120px;
      border-radius: 50%;
      opacity: 0.05;
      transform: translate(30%, -30%);
    }
    
    &.danger {
      border-left: 4px solid #ef4444;
      .stat-bg { background: #ef4444; }
    }
    
    &.warning {
      border-left: 4px solid #f59e0b;
      .stat-bg { background: #f59e0b; }
    }
    
    &.success {
      border-left: 4px solid #22c55e;
      .stat-bg { background: #22c55e; }
    }
    
    .stat-content {
      display: flex;
      align-items: center;
      gap: 16px;
      position: relative;
      z-index: 1;
      
      .stat-icon-wrapper {
        width: 56px;
        height: 56px;
        border-radius: 14px;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;
        
        &.danger {
          background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
        }
        
        &.warning {
          background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
        }
        
        &.success {
          background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
        }
        
        .el-icon {
          color: #ffffff;
        }
      }
      
      .stat-info {
        flex: 1;
        
        .stat-value {
          font-size: 32px;
          font-weight: 700;
          color: #1e293b;
          margin-bottom: 4px;
        }
        
        .stat-label {
          font-size: 14px;
          color: #64748b;
        }
      }
      
      .stat-trend {
        display: flex;
        align-items: center;
        gap: 6px;
        padding: 6px 12px;
        background: #fef2f2;
        border-radius: 20px;
        font-size: 12px;
        font-weight: 600;
        color: #ef4444;
        
        .trend-icon {
          font-size: 14px;
        }
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
    animation: slideUp 0.6s ease-out;
    animation-delay: 0.3s;
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
      
      .search-input, .search-select {
        width: 200px;
      }
    }
    
    .search-btn {
      background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
      border: none;
      border-radius: 10px;
      padding: 10px 20px;
      font-weight: 500;
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 20px rgba(239, 68, 68, 0.4);
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
  animation-delay: 0.4s;
  animation-fill-mode: both;
  
  .table-card {
    background: #ffffff;
    border-radius: 20px;
    padding: 24px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24px;
      
      .header-left {
        display: flex;
        align-items: center;
        gap: 16px;
        
        .header-icon-small {
          width: 48px;
          height: 48px;
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          
          &.danger {
            background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
          }
          
          .el-icon {
            color: #ffffff;
            font-size: 24px;
          }
        }
        
        .header-text {
          h3 {
            font-size: 18px;
            font-weight: 600;
            color: #1e293b;
            margin: 0 0 4px 0;
          }
          
          p {
            font-size: 14px;
            color: #64748b;
            margin: 0;
          }
        }
      }
      
      .header-right {
        .live-tag {
          padding: 8px 16px;
          border-radius: 20px;
          font-weight: 600;
          
          .el-icon {
            margin-right: 6px;
            animation: pulse 1.5s ease-in-out infinite;
          }
        }
      }
    }
  }
}

// 用户信息单元格
.user-cell {
  display: flex;
  align-items: center;
  gap: 12px;
  
  .user-avatar {
    flex-shrink: 0;
    font-weight: 600;
    
    &.emergency {
      background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
      color: #ffffff;
    }
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
      gap: 6px;
      font-size: 13px;
      color: #64748b;
      
      .el-icon {
        color: #ef4444;
      }
    }
  }
}

// 位置信息
.location-info {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  
  .location-icon {
    width: 32px;
    height: 32px;
    background: #fef2f2;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    
    .el-icon {
      color: #ef4444;
    }
  }
  
  .location-text {
    flex: 1;
    font-size: 14px;
    color: #475569;
    line-height: 1.5;
    padding-top: 6px;
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
  padding: 6px 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
  
  .el-icon {
    color: #94a3b8;
  }
  
  &.urgent {
    background: #fef2f2;
    color: #ef4444;
    font-weight: 600;
    animation: pulse 2s ease-in-out infinite;
    
    .el-icon {
      color: #ef4444;
    }
  }
}

// 状态徽章
.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  position: relative;
  overflow: hidden;
  
  &.danger {
    background: #fef2f2;
    color: #ef4444;
    
    .status-icon {
      animation: shake 0.5s ease-in-out infinite;
    }
  }
  
  &.warning {
    background: #fffbeb;
    color: #d97706;
    
    .status-icon {
      animation: spin 1s linear infinite;
    }
  }
  
  &.success {
    background: #f0fdf4;
    color: #16a34a;
  }
  
  .status-pulse {
    position: absolute;
    width: 100%;
    height: 100%;
    background: rgba(239, 68, 68, 0.2);
    border-radius: 20px;
    animation: pulse 1.5s ease-in-out infinite;
  }
  
  .status-icon {
    position: relative;
    z-index: 1;
  }
  
  .status-text {
    position: relative;
    z-index: 1;
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
    
    &.process {
      background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
      border: none;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(34, 197, 94, 0.4);
      }
    }
    
    &.complete {
      background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
      border: none;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
      }
    }
    
    &.location {
      background: linear-gradient(135deg, #6b7280 0%, #4b5563 100%);
      border: none;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(107, 114, 128, 0.4);
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
        background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
      }
    }
  }
}

// 紧急行样式
:deep(.urgent-row) {
  background: #fef2f2 !important;
  animation: urgentPulse 2s ease-in-out infinite;
}

// 位置对话框
.location-dialog {
  :deep(.el-dialog__header) {
    background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
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
  
  .location-content {
    .location-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 24px;
      border-bottom: 1px solid #e2e8f0;
      
      .user-info-large {
        display: flex;
        align-items: center;
        gap: 16px;
        
        .user-avatar-large {
          font-weight: 600;
          
          &.emergency {
            background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
            color: #ffffff;
          }
        }
        
        .user-meta {
          .user-name-large {
            font-size: 18px;
            font-weight: 600;
            color: #1e293b;
            margin-bottom: 4px;
          }
          
          .user-phone-large {
            display: flex;
            align-items: center;
            gap: 6px;
            font-size: 14px;
            color: #64748b;
            
            .el-icon {
              color: #ef4444;
            }
          }
        }
      }
      
      .call-time {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 14px;
        color: #64748b;
        padding: 8px 16px;
        background: #f8fafc;
        border-radius: 8px;
        
        .el-icon {
          color: #ef4444;
        }
      }
    }
    
    .location-map {
      height: 300px;
      background: #f1f5f9;
      display: flex;
      align-items: center;
      justify-content: center;
      
      .map-placeholder {
        text-align: center;
        color: #94a3b8;
        
        .el-icon {
          color: #cbd5e1;
          margin-bottom: 16px;
        }
        
        p {
          margin: 0;
          font-size: 16px;
          color: #64748b;
        }
        
        .location-address {
          font-size: 14px;
          color: #94a3b8;
          margin-top: 8px;
        }
      }
    }
    
    .location-actions {
      display: flex;
      justify-content: center;
      gap: 16px;
      padding: 24px;
      
      .action-btn-navigate {
        background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
        border: none;
        border-radius: 10px;
        padding: 12px 24px;
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 8px 20px rgba(59, 130, 246, 0.4);
        }
      }
      
      .action-btn-call {
        background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
        border: none;
        border-radius: 10px;
        padding: 12px 24px;
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 8px 20px rgba(34, 197, 94, 0.4);
        }
      }
    }
  }
}

// 表格行悬停效果
:deep(.table-row) {
  transition: all 0.3s ease;
  
  &:hover {
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

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
}

@keyframes shake {
  0%, 100% { transform: rotate(0deg); }
  25% { transform: rotate(-10deg); }
  75% { transform: rotate(10deg); }
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes urgentPulse {
  0%, 100% { background: #fef2f2; }
  50% { background: #fee2e2; }
}

// 响应式设计
@media (max-width: 768px) {
  .emergency-page {
    padding: 16px;
  }
  
  .emergency-banner {
    .banner-content {
      flex-direction: column;
      text-align: center;
      gap: 12px;
    }
  }
  
  .page-header {
    flex-direction: column;
    gap: 20px;
    text-align: center;
    
    .header-icon {
      width: 60px;
      height: 60px;
    }
  }
  
  .stats-section {
    .el-col {
      margin-bottom: 16px;
    }
  }
  
  .search-form-wrapper {
    .search-form {
      .el-form-item {
        margin-right: 0;
        margin-bottom: 12px;
        width: 100%;
        
        .search-input, .search-select {
          width: 100%;
        }
      }
    }
  }
}
</style>
