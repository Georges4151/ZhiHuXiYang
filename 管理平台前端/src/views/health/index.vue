<template>
  <div class="health-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">健康档案</h1>
        <p class="page-subtitle">管理用户健康监测数据</p>
      </div>
      <div class="header-icon">
        <el-icon :size="48"><FirstAidKit /></el-icon>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="stat-card" style="animation-delay: 0s;">
            <div class="stat-bg" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);"></div>
            <div class="stat-content">
              <div class="stat-icon-wrapper" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
                <el-icon :size="24"><Document /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.totalRecords }}</div>
                <div class="stat-label">健康记录数</div>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="stat-card" style="animation-delay: 0.1s;">
            <div class="stat-bg" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);"></div>
            <div class="stat-content">
              <div class="stat-icon-wrapper" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
                <el-icon :size="24"><User /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.monitoringUsers }}</div>
                <div class="stat-label">监测用户数</div>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="stat-card" style="animation-delay: 0.2s;">
            <div class="stat-bg" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);"></div>
            <div class="stat-content">
              <div class="stat-icon-wrapper" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);">
                <el-icon :size="24"><Warning /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.abnormalCount }}</div>
                <div class="stat-label">异常提醒</div>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="stat-card" style="animation-delay: 0.3s;">
            <div class="stat-bg" style="background: linear-gradient(135deg, #30cfd0 0%, #330867 100%);"></div>
            <div class="stat-content">
              <div class="stat-icon-wrapper" style="background: linear-gradient(135deg, #30cfd0 0%, #330867 100%);">
                <el-icon :size="24"><Timer /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.onTimeRate }}%</div>
                <div class="stat-label">按时测量率</div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 数据表格 -->
    <div class="table-section">
      <div class="table-card">
        <div class="card-header">
          <div class="header-left">
            <div class="header-icon-small" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
              <el-icon><FirstAidKit /></el-icon>
            </div>
            <div class="header-text">
              <h3>健康数据记录</h3>
              <p>用户健康监测数据详情</p>
            </div>
          </div>
        </div>
        <el-table 
          :data="healthList" 
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
          <el-table-column label="测量时间" width="160" align="center">
            <template #default="{ row }">
              <div class="time-info">
                <el-icon><Clock /></el-icon>
                <span>{{ formatDateTime(row.measureTime) }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="血压" width="140" align="center">
            <template #default="{ row }">
              <div class="bp-value" :class="getBPClass(row.bloodPressureHigh, row.bloodPressureLow)">
                <div class="bp-numbers">
                  <span class="bp-high">{{ row.bloodPressureHigh }}</span>
                  <span class="bp-divider">/</span>
                  <span class="bp-low">{{ row.bloodPressureLow }}</span>
                </div>
                <span class="bp-unit">mmHg</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="血糖" width="120" align="center">
            <template #default="{ row }">
              <div class="sugar-value" :class="getSugarClass(row.bloodSugar)">
                <span class="sugar-num">{{ row.bloodSugar }}</span>
                <span class="sugar-unit">mmol/L</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="心率" width="120" align="center">
            <template #default="{ row }">
              <div class="heart-value" :class="getHeartClass(row.heartRate)">
                <div class="heart-icon">
                  <el-icon><HeartFilled /></el-icon>
                </div>
                <span class="heart-num">{{ row.heartRate }}</span>
                <span class="heart-unit">次/分</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="备注" min-width="200" show-overflow-tooltip>
            <template #default="{ row }">
              <div class="remark-text" v-if="row.remark">
                <el-icon><InfoFilled /></el-icon>
                <span>{{ row.remark }}</span>
              </div>
              <span v-else class="no-remark">-</span>
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
            @size-change="loadHealth" 
            @current-change="loadHealth" 
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getHealthList, getHealthStats } from '@/api/health'

const loading = ref(false)
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const healthList = ref([])
const stats = ref({ totalRecords: 0, monitoringUsers: 0, abnormalCount: 0, onTimeRate: 0 })

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

const getBPClass = (high, low) => {
  if (high > 140 || low > 90) return 'warning'
  if (high < 90 || low < 60) return 'danger'
  return 'normal'
}

const getSugarClass = (sugar) => {
  if (sugar > 7.0) return 'warning'
  if (sugar < 3.9) return 'danger'
  return 'normal'
}

const getHeartClass = (rate) => {
  if (rate > 100) return 'warning'
  if (rate < 60) return 'danger'
  return 'normal'
}

const loadHealth = async () => {
  loading.value = true
  try {
    const res = await getHealthList({ page: pagination.page, pageSize: pagination.pageSize })
    healthList.value = res.list || []
    pagination.total = res.total || 0
  } catch (error) {
    console.error('加载健康数据失败', error)
  } finally {
    loading.value = false
  }
}

const loadStats = async () => {
  try {
    stats.value = await getHealthStats()
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

onMounted(() => { loadHealth(); loadStats() })
</script>

<style lang="scss" scoped>
.health-page {
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
      width: 100px;
      height: 100px;
      border-radius: 50%;
      opacity: 0.1;
      transform: translate(30%, -30%);
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
        
        .el-icon {
          color: #ffffff;
        }
      }
      
      .stat-info {
        .stat-value {
          font-size: 28px;
          font-weight: 700;
          color: #1e293b;
          margin-bottom: 4px;
        }
        
        .stat-label {
          font-size: 14px;
          color: #64748b;
        }
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
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
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

// 时间信息
.time-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 13px;
  color: #64748b;
  
  .el-icon {
    color: #f093fb;
  }
}

// 血压值
.bp-value {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  padding: 8px 12px;
  border-radius: 12px;
  
  &.normal {
    background: #dcfce7;
    .bp-high, .bp-low { color: #15803d; }
  }
  
  &.warning {
    background: #fef3c7;
    .bp-high, .bp-low { color: #b45309; }
  }
  
  &.danger {
    background: #fee2e2;
    .bp-high, .bp-low { color: #dc2626; }
  }
  
  .bp-numbers {
    display: flex;
    align-items: center;
    gap: 4px;
    
    .bp-high, .bp-low {
      font-weight: 700;
      font-size: 16px;
    }
    
    .bp-divider {
      color: #94a3b8;
    }
  }
  
  .bp-unit {
    font-size: 11px;
    color: #94a3b8;
  }
}

// 血糖值
.sugar-value {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  padding: 8px 12px;
  border-radius: 12px;
  
  &.normal {
    background: #dcfce7;
    .sugar-num { color: #15803d; }
  }
  
  &.warning {
    background: #fef3c7;
    .sugar-num { color: #b45309; }
  }
  
  &.danger {
    background: #fee2e2;
    .sugar-num { color: #dc2626; }
  }
  
  .sugar-num {
    font-weight: 700;
    font-size: 16px;
  }
  
  .sugar-unit {
    font-size: 11px;
    color: #94a3b8;
  }
}

// 心率值
.heart-value {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  padding: 8px 12px;
  border-radius: 12px;
  
  &.normal {
    background: #dcfce7;
    .heart-num { color: #15803d; }
    .heart-icon { color: #22c55e; }
  }
  
  &.warning {
    background: #fef3c7;
    .heart-num { color: #b45309; }
    .heart-icon { color: #f59e0b; }
  }
  
  &.danger {
    background: #fee2e2;
    .heart-num { color: #dc2626; }
    .heart-icon { color: #ef4444; }
  }
  
  .heart-icon {
    font-size: 18px;
    animation: heartbeat 1.5s ease-in-out infinite;
  }
  
  .heart-num {
    font-weight: 700;
    font-size: 16px;
  }
  
  .heart-unit {
    font-size: 11px;
    color: #94a3b8;
  }
}

// 备注
.remark-text {
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

.no-remark {
  color: #94a3b8;
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

@keyframes heartbeat {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

// 响应式设计
@media (max-width: 768px) {
  .health-page {
    padding: 16px;
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
}
</style>
