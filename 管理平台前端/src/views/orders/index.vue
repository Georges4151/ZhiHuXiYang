<template>
  <div class="orders-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">订单管理</h1>
        <p class="page-subtitle">管理平台所有服务订单</p>
      </div>
      <div class="header-stats">
        <div class="stat-item">
          <span class="stat-value">{{ stats.pending }}</span>
          <span class="stat-label">待处理</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ stats.processing }}</span>
          <span class="stat-label">进行中</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ stats.completed }}</span>
          <span class="stat-label">已完成</span>
        </div>
      </div>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <div class="search-form-wrapper">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="订单号">
            <el-input 
              v-model="searchForm.orderNo" 
              placeholder="请输入订单号" 
              clearable
              prefix-icon="Document"
              class="search-input"
            />
          </el-form-item>
          <el-form-item label="订单状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable class="search-select">
              <el-option label="待处理" :value="0">
                <el-tag type="info" size="small" effect="light">待处理</el-tag>
              </el-option>
              <el-option label="进行中" :value="1">
                <el-tag type="warning" size="small" effect="light">进行中</el-tag>
              </el-option>
              <el-option label="已完成" :value="2">
                <el-tag type="success" size="small" effect="light">已完成</el-tag>
              </el-option>
              <el-option label="已取消" :value="3">
                <el-tag type="danger" size="small" effect="light">已取消</el-tag>
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
          :data="orderList" 
          v-loading="loading" 
          style="width: 100%"
          :header-cell-style="headerCellStyle"
          row-class-name="table-row"
        >
          <el-table-column type="index" label="#" width="60" align="center" />
          <el-table-column label="订单信息" min-width="220">
            <template #default="{ row }">
              <div class="order-cell">
                <div class="order-icon" :class="getStatusClass(row.orderStatus)">
                  <el-icon><component :is="getStatusIcon(row.orderStatus)" /></el-icon>
                </div>
                <div class="order-info">
                  <div class="order-no">{{ row.orderNo }}</div>
                  <div class="order-service">{{ row.serviceName }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="用户信息" min-width="160">
            <template #default="{ row }">
              <div class="user-cell">
                <el-avatar :size="32" :src="row.userAvatar" class="user-avatar">
                  {{ row.contactName?.charAt(0) || 'U' }}
                </el-avatar>
                <div class="user-info">
                  <div class="user-name">{{ row.contactName }}</div>
                  <div class="user-phone">
                    <el-icon><Phone /></el-icon>
                    {{ row.contactPhone }}
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="金额" width="120" align="center">
            <template #default="{ row }">
              <div class="amount-cell">
                <span class="amount-prefix">¥</span>
                <span class="amount-value">{{ row.amount }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="服务时间" width="160" align="center">
            <template #default="{ row }">
              <div class="time-cell">
                <el-icon><Clock /></el-icon>
                <span>{{ formatTime(row.appointmentTime) }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="服务地址" min-width="200" show-overflow-tooltip>
            <template #default="{ row }">
              <div class="address-cell">
                <el-icon><Location /></el-icon>
                <span>{{ row.serviceAddress }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="110" align="center">
            <template #default="{ row }">
              <div class="status-badge" :class="getStatusClass(row.orderStatus)">
                <el-icon><component :is="getStatusIcon(row.orderStatus)" /></el-icon>
                <span>{{ getStatusText(row.orderStatus) }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="下单时间" width="160" align="center">
            <template #default="{ row }">
              <div class="time-cell">
                <el-icon><Calendar /></el-icon>
                <span>{{ formatTime(row.createTime) }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right" align="center">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-tooltip content="查看详情" placement="top">
                  <el-button type="primary" link class="action-btn view" @click="handleView(row)">
                    <el-icon><View /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip content="分配人员" placement="top" v-if="row.orderStatus === 0">
                  <el-button type="success" link class="action-btn assign" @click="handleAssign(row)">
                    <el-icon><UserFilled /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip content="完成订单" placement="top" v-if="row.orderStatus === 1">
                  <el-button type="success" link class="action-btn complete" @click="handleComplete(row)">
                    <el-icon><CircleCheck /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip content="取消订单" placement="top" v-if="row.orderStatus === 0 || row.orderStatus === 1">
                  <el-button type="danger" link class="action-btn cancel" @click="handleCancel(row)">
                    <el-icon><CircleClose /></el-icon>
                  </el-button>
                </el-tooltip>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <!-- 空状态 -->
        <div v-if="orderList.length === 0 && !loading" class="empty-state">
          <el-icon :size="64"><Document /></el-icon>
          <p>暂无订单数据</p>
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

    <!-- 订单详情对话框 -->
    <el-dialog 
      v-model="detailVisible" 
      title="订单详情" 
      width="700px"
      class="order-dialog"
      destroy-on-close
    >
      <div class="order-detail" v-if="orderDetail.id">
        <div class="detail-header">
          <div class="detail-status" :class="getStatusClass(orderDetail.orderStatus)">
            <el-icon><component :is="getStatusIcon(orderDetail.orderStatus)" /></el-icon>
            <span>{{ getStatusText(orderDetail.orderStatus) }}</span>
          </div>
          <div class="detail-amount">
            <span class="amount-label">订单金额</span>
            <span class="amount-value">¥{{ orderDetail.amount }}</span>
          </div>
        </div>

        <div class="detail-section">
          <h4 class="section-title">
            <el-icon><Document /></el-icon>
            订单信息
          </h4>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="item-label">订单号</span>
              <span class="item-value">{{ orderDetail.orderNo }}</span>
            </div>
            <div class="detail-item">
              <span class="item-label">服务名称</span>
              <span class="item-value">{{ orderDetail.serviceName }}</span>
            </div>
            <div class="detail-item">
              <span class="item-label">下单时间</span>
              <span class="item-value">{{ orderDetail.createTime }}</span>
            </div>
            <div class="detail-item">
              <span class="item-label">服务时间</span>
              <span class="item-value">{{ orderDetail.appointmentTime }}</span>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h4 class="section-title">
            <el-icon><User /></el-icon>
            用户信息
          </h4>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="item-label">联系人</span>
              <span class="item-value">{{ orderDetail.contactName }}</span>
            </div>
            <div class="detail-item">
              <span class="item-label">联系电话</span>
              <span class="item-value">{{ orderDetail.contactPhone }}</span>
            </div>
            <div class="detail-item full-width">
              <span class="item-label">服务地址</span>
              <span class="item-value">{{ orderDetail.serviceAddress }}</span>
            </div>
          </div>
        </div>

        <div class="detail-section" v-if="orderDetail.workerName">
          <h4 class="section-title">
            <el-icon><UserFilled /></el-icon>
            服务人员
          </h4>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="item-label">服务人员</span>
              <span class="item-value">{{ orderDetail.workerName }}</span>
            </div>
            <div class="detail-item">
              <span class="item-label">联系电话</span>
              <span class="item-value">{{ orderDetail.workerPhone || '暂无' }}</span>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 分配人员对话框 -->
    <el-dialog 
      v-model="assignVisible" 
      title="分配服务人员" 
      width="450px"
      class="assign-dialog"
    >
      <div class="assign-content">
        <el-form :model="assignForm" label-width="100px" class="assign-form">
          <el-form-item label="服务人员">
            <el-select v-model="assignForm.workerId" placeholder="请选择服务人员" style="width: 100%">
              <el-option label="张师傅" :value="1">
                <div class="worker-option">
                  <el-avatar :size="28" class="worker-avatar">张</el-avatar>
                  <span>张师傅</span>
                  <el-tag type="success" size="small" effect="light">空闲</el-tag>
                </div>
              </el-option>
              <el-option label="李师傅" :value="2">
                <div class="worker-option">
                  <el-avatar :size="28" class="worker-avatar">李</el-avatar>
                  <span>李师傅</span>
                  <el-tag type="success" size="small" effect="light">空闲</el-tag>
                </div>
              </el-option>
              <el-option label="王师傅" :value="3">
                <div class="worker-option">
                  <el-avatar :size="28" class="worker-avatar">王</el-avatar>
                  <span>王师傅</span>
                  <el-tag type="warning" size="small" effect="light">忙碌</el-tag>
                </div>
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button class="cancel-btn" @click="assignVisible = false">取消</el-button>
          <el-button type="primary" class="submit-btn" @click="handleAssignSubmit">
            <el-icon><Check /></el-icon>
            <span>确定分配</span>
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderList, getOrderDetail, updateOrderStatus } from '@/api/order'


const loading = ref(false)
const detailVisible = ref(false)
const assignVisible = ref(false)

const searchForm = reactive({
  orderNo: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const orderList = ref([])
const orderDetail = ref({})
const assignForm = reactive({
  orderId: null,
  workerId: null
})

const stats = computed(() => {
  return {
    pending: orderList.value.filter(o => o.orderStatus === 0).length,
    processing: orderList.value.filter(o => o.orderStatus === 1).length,
    completed: orderList.value.filter(o => o.orderStatus === 2).length
  }
})

const headerCellStyle = {
  background: '#f8fafc',
  color: '#475569',
  fontWeight: 600,
  fontSize: '13px'
}

const getStatusType = (status) => {
  const types = { 0: 'info', 1: 'warning', 2: 'success', 3: 'danger' }
  return types[status] || 'info'
}

const getStatusClass = (status) => {
  const classes = { 0: 'pending', 1: 'processing', 2: 'completed', 3: 'cancelled' }
  return classes[status] || 'pending'
}

const getStatusIcon = (status) => {
  const icons = { 
    0: 'Timer', 
    1: 'Loading', 
    2: 'CheckCircle', 
    3: 'CircleClose' 
  }
  return icons[status] || 'Timer'
}

const getStatusText = (status) => {
  const texts = { 0: '待处理', 1: '进行中', 2: '已完成', 3: '已取消' }
  return texts[status] || '未知'
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
  loadOrders()
}

const handleReset = () => {
  searchForm.orderNo = ''
  searchForm.status = ''
  handleSearch()
}

const handleSizeChange = () => loadOrders()
const handleCurrentChange = () => loadOrders()

const loadOrders = async () => {
  loading.value = true
  try {
    const res = await getOrderList({
      page: pagination.page,
      pageSize: pagination.pageSize,
      ...searchForm
    })
    orderList.value = res.list || []
    pagination.total = res.total || 0
  } catch (error) {
    console.error('加载订单列表失败', error)
  } finally {
    loading.value = false
  }
}

const handleView = async (row) => {
  try {
    const res = await getOrderDetail(row.id)
    orderDetail.value = res || row
    detailVisible.value = true
  } catch (error) {
    console.error('获取订单详情失败', error)
  }
}

const handleAssign = (row) => {
  assignForm.orderId = row.id
  assignForm.workerId = null
  assignVisible.value = true
}

const handleAssignSubmit = async () => {
  if (!assignForm.workerId) {
    ElMessage.warning('请选择服务人员')
    return
  }
  try {
    await updateOrderStatus(assignForm.orderId, 1)
    ElMessage.success('分配成功')
    assignVisible.value = false
    loadOrders()
  } catch (error) {
    console.error('分配失败', error)
  }
}

const handleComplete = (row) => {
  ElMessageBox.confirm('确定要将该订单标记为完成吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    customClass: 'custom-message-box'
  }).then(async () => {
    await updateOrderStatus(row.id, 2)
    ElMessage.success('订单已完成')
    loadOrders()
  }).catch(() => {})
}

const handleCancel = (row) => {
  ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    customClass: 'custom-message-box'
  }).then(async () => {
    await updateOrderStatus(row.id, 3)
    ElMessage.success('订单已取消')
    loadOrders()
  }).catch(() => {})
}

onMounted(() => loadOrders())
</script>

<style lang="scss" scoped>
.orders-page {
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

  .header-stats {
    display: flex;
    gap: 24px;

    .stat-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 12px 24px;
      background: #fff;
      border-radius: 12px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

      .stat-value {
        font-size: 24px;
        font-weight: 700;
        color: #667eea;
      }

      .stat-label {
        font-size: 12px;
        color: #64748b;
        margin-top: 4px;
      }
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

  // 订单单元格
  .order-cell {
    display: flex;
    align-items: center;
    gap: 12px;

    .order-icon {
      width: 40px;
      height: 40px;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;

      &.pending {
        background: #fef3c7;
        color: #f59e0b;
      }

      &.processing {
        background: #dbeafe;
        color: #3b82f6;
      }

      &.completed {
        background: #d1fae5;
        color: #10b981;
      }

      &.cancelled {
        background: #fee2e2;
        color: #ef4444;
      }
    }

    .order-info {
      .order-no {
        font-weight: 600;
        color: #1e293b;
        font-size: 14px;
        margin-bottom: 4px;
      }

      .order-service {
        font-size: 12px;
        color: #64748b;
      }
    }
  }

  // 用户单元格
  .user-cell {
    display: flex;
    align-items: center;
    gap: 10px;

    .user-avatar {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: #fff;
      font-weight: 600;
    }

    .user-info {
      .user-name {
        font-weight: 500;
        color: #1e293b;
        font-size: 14px;
        margin-bottom: 2px;
      }

      .user-phone {
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
  }

  // 金额单元格
  .amount-cell {
    display: flex;
    align-items: baseline;
    justify-content: center;
    gap: 2px;

    .amount-prefix {
      font-size: 14px;
      color: #ef4444;
      font-weight: 500;
    }

    .amount-value {
      font-size: 18px;
      color: #ef4444;
      font-weight: 700;
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

  // 状态徽章
  .status-badge {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 6px 12px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 500;

    &.pending {
      background: #fef3c7;
      color: #f59e0b;
    }

    &.processing {
      background: #dbeafe;
      color: #3b82f6;
    }

    &.completed {
      background: #d1fae5;
      color: #10b981;
    }

    &.cancelled {
      background: #fee2e2;
      color: #ef4444;
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

      &.assign {
        color: #10b981;
        background: #d1fae5;

        &:hover {
          background: #10b981;
          color: #fff;
        }
      }

      &.complete {
        color: #10b981;
        background: #d1fae5;

        &:hover {
          background: #10b981;
          color: #fff;
        }
      }

      &.cancel {
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
      margin: 0;
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

// 订单详情对话框
:deep(.order-dialog) {
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
    padding: 0;
  }

  .order-detail {
    .detail-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 24px;
      background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);

      .detail-status {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 8px 16px;
        border-radius: 20px;
        font-size: 14px;
        font-weight: 500;

        &.pending {
          background: #fef3c7;
          color: #f59e0b;
        }

        &.processing {
          background: #dbeafe;
          color: #3b82f6;
        }

        &.completed {
          background: #d1fae5;
          color: #10b981;
        }

        &.cancelled {
          background: #fee2e2;
          color: #ef4444;
        }
      }

      .detail-amount {
        display: flex;
        flex-direction: column;
        align-items: flex-end;

        .amount-label {
          font-size: 12px;
          color: #64748b;
          margin-bottom: 4px;
        }

        .amount-value {
          font-size: 28px;
          font-weight: 700;
          color: #ef4444;
        }
      }
    }

    .detail-section {
      padding: 24px;
      border-bottom: 1px solid #f1f5f9;

      &:last-child {
        border-bottom: none;
      }

      .section-title {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 14px;
        font-weight: 600;
        color: #475569;
        margin: 0 0 16px;

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

          &.full-width {
            grid-column: 1 / -1;
          }

          .item-label {
            font-size: 12px;
            color: #94a3b8;
          }

          .item-value {
            font-size: 14px;
            font-weight: 500;
            color: #1e293b;
          }
        }
      }
    }
  }
}

// 分配对话框
:deep(.assign-dialog) {
  .el-dialog__header {
    padding: 20px 24px;
    border-bottom: 1px solid #f1f5f9;
    margin-right: 0;

    .el-dialog__title {
      font-weight: 600;
      color: #1e293b;
    }
  }

  .assign-content {
    padding: 24px;

    .assign-form {
      .el-form-item {
        margin-bottom: 0;

        .el-form-item__label {
          font-weight: 500;
          color: #475569;
        }
      }
    }
  }

  .worker-option {
    display: flex;
    align-items: center;
    gap: 8px;

    .worker-avatar {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: #fff;
      font-size: 12px;
    }
  }

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    padding: 20px 24px;
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

// 响应式
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;

    .header-stats {
      width: 100%;
      justify-content: space-between;
    }
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