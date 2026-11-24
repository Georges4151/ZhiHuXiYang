<template>
  <div class="dashboard">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">数据概览</h1>
        <p class="page-subtitle">实时监控平台运营数据</p>
      </div>
      <div class="header-actions">
        <el-button class="refresh-btn" @click="refreshData">
          <el-icon><Refresh /></el-icon>
          <span>刷新数据</span>
        </el-button>
        <el-button type="primary" class="export-btn">
          <el-icon><Download /></el-icon>
          <span>导出报表</span>
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :lg="6" v-for="(item, index) in statsCards" :key="item.key">
          <div class="stat-card" :style="{ animationDelay: `${index * 0.1}s` }">
            <div class="stat-bg" :style="{ background: item.gradient }"></div>
            <div class="stat-content">
              <div class="stat-icon-wrapper" :style="{ background: item.gradient }">
                <el-icon :size="24">
                  <component :is="item.icon" />
                </el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ item.value }}</div>
                <div class="stat-label">{{ item.label }}</div>
                <div class="stat-trend" :class="item.trend > 0 ? 'up' : 'down'">
                  <el-icon>
                    <ArrowUp v-if="item.trend > 0" />
                    <ArrowDown v-else />
                  </el-icon>
                  <span>{{ Math.abs(item.trend) }}%</span>
                  <span class="trend-label">较上月</span>
                </div>
              </div>
            </div>
            <div class="stat-decoration">
              <div class="decoration-circle"></div>
              <div class="decoration-ring"></div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <el-row :gutter="20">
        <el-col :xs="24" :lg="16">
          <div class="chart-card main-chart">
            <div class="card-header">
              <div class="header-left">
                <div class="header-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
                  <el-icon><TrendCharts /></el-icon>
                </div>
                <div class="header-text">
                  <h3>服务订单趋势</h3>
                  <p>近7天订单数据变化</p>
                </div>
              </div>
              <div class="header-actions">
                <el-radio-group v-model="timeRange" size="small">
                  <el-radio-button label="week">本周</el-radio-button>
                  <el-radio-button label="month">本月</el-radio-button>
                  <el-radio-button label="year">全年</el-radio-button>
                </el-radio-group>
              </div>
            </div>
            <div ref="orderChartRef" class="chart-container"></div>
          </div>
        </el-col>
        <el-col :xs="24" :lg="8">
          <div class="chart-card side-chart">
            <div class="card-header">
              <div class="header-left">
                <div class="header-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
                  <el-icon><PieChart /></el-icon>
                </div>
                <div class="header-text">
                  <h3>服务类型分布</h3>
                  <p>各类服务占比统计</p>
                </div>
              </div>
            </div>
            <div ref="serviceChartRef" class="chart-container"></div>
            <div class="chart-legend">
              <div class="legend-item" v-for="(item, index) in serviceLegend" :key="index">
                <div class="legend-dot" :style="{ background: item.color }"></div>
                <span class="legend-name">{{ item.name }}</span>
                <span class="legend-value">{{ item.value }}%</span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 数据列表区域 -->
    <div class="lists-section">
      <el-row :gutter="20">
        <el-col :xs="24" :lg="12">
          <div class="list-card">
            <div class="card-header">
              <div class="header-left">
                <div class="header-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
                  <el-icon><DocumentChecked /></el-icon>
                </div>
                <div class="header-text">
                  <h3>最新订单</h3>
                  <p>实时更新的订单数据</p>
                </div>
              </div>
              <el-button type="primary" link class="view-all-btn" @click="$router.push('/orders')">
                查看全部
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
            <div class="list-content">
              <div class="order-item" v-for="(order, index) in recentOrders" :key="order.id" :style="{ animationDelay: `${index * 0.05}s` }">
                <div class="order-icon" :class="getStatusClass(order.status)">
                  <el-icon><component :is="getStatusIcon(order.status)" /></el-icon>
                </div>
                <div class="order-info">
                  <div class="order-main">
                    <span class="order-no">{{ order.orderNo }}</span>
                    <span class="order-service">{{ order.serviceName }}</span>
                  </div>
                  <div class="order-meta">
                    <span class="order-user">
                      <el-icon><User /></el-icon>
                      {{ order.userName }}
                    </span>
                    <span class="order-time">{{ order.createdAt }}</span>
                  </div>
                </div>
                <div class="order-status">
                  <span class="status-badge" :class="getStatusClass(order.status)">
                    {{ getStatusText(order.status) }}
                  </span>
                </div>
              </div>
              <div v-if="recentOrders.length === 0" class="empty-state">
                <el-icon :size="48"><Document /></el-icon>
                <p>暂无订单数据</p>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :lg="12">
          <div class="list-card">
            <div class="card-header">
              <div class="header-left">
                <div class="header-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
                  <el-icon><UserFilled /></el-icon>
                </div>
                <div class="header-text">
                  <h3>最新用户</h3>
                  <p>最近注册的用户信息</p>
                </div>
              </div>
              <el-button type="primary" link class="view-all-btn" @click="$router.push('/users')">
                查看全部
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
            <div class="list-content">
              <div class="user-item" v-for="(user, index) in recentUsers" :key="user.id" :style="{ animationDelay: `${index * 0.05}s` }">
                <div class="user-avatar">
                  <el-avatar :size="40" :src="user.avatar">
                    {{ user.name?.charAt(0) || 'U' }}
                  </el-avatar>
                  <div class="online-indicator"></div>
                </div>
                <div class="user-info">
                  <div class="user-main">
                    <span class="user-name">{{ user.name }}</span>
                    <span class="user-age">{{ user.age }}岁</span>
                  </div>
                  <div class="user-meta">
                    <span class="user-phone">
                      <el-icon><Phone /></el-icon>
                      {{ user.phone }}
                    </span>
                  </div>
                </div>
                <div class="user-time">
                  <el-icon><Clock /></el-icon>
                  <span>{{ formatTime(user.createdAt) }}</span>
                </div>
              </div>
              <div v-if="recentUsers.length === 0" class="empty-state">
                <el-icon :size="48"><User /></el-icon>
                <p>暂无用户数据</p>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 快捷入口 -->
    <div class="quick-actions">
      <h3 class="section-title">快捷入口</h3>
      <el-row :gutter="16">
        <el-col :xs="12" :sm="8" :md="6" :lg="4" v-for="(action, index) in quickActions" :key="index">
          <div class="action-card" @click="$router.push(action.path)" :style="{ animationDelay: `${index * 0.05}s` }">
            <div class="action-icon" :style="{ background: action.gradient }">
              <el-icon :size="24">
                <component :is="action.icon" />
              </el-icon>
            </div>
            <span class="action-name">{{ action.name }}</span>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { getOverview, getOrderTrend, getServiceDistribution, getRecentOrders, getRecentUsers } from '@/api/stats'
import { 
  User, List, Service, Star, Refresh, Download, 
  ArrowUp, ArrowDown, TrendCharts, PieChart, 
  DocumentChecked, ArrowRight, UserFilled, Document,
  Phone, Clock, CircleCheck, Timer, Loading, Plus, Setting, Bell
} from '@element-plus/icons-vue'

const orderChartRef = ref(null)
const serviceChartRef = ref(null)
let orderChart = null
let serviceChart = null
const timeRange = ref('week')

const statsCards = ref([
  { 
    key: 'users', 
    label: '注册用户', 
    value: '0', 
    icon: 'User', 
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    trend: 12.5
  },
  { 
    key: 'orders', 
    label: '服务订单', 
    value: '0', 
    icon: 'List', 
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    trend: 8.3
  },
  { 
    key: 'services', 
    label: '服务项目', 
    value: '0', 
    icon: 'Service', 
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    trend: -2.1
  },
  { 
    key: 'satisfaction', 
    label: '满意度', 
    value: '0%', 
    icon: 'Star', 
    gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    trend: 5.7
  }
])

const recentOrders = ref([])
const recentUsers = ref([])

const serviceLegend = ref([
  { name: '健康服务', value: 35, color: '#667eea' },
  { name: '家政服务', value: 25, color: '#f093fb' },
  { name: '餐饮服务', value: 20, color: '#4facfe' },
  { name: '陪护服务', value: 15, color: '#43e97b' },
  { name: '其他服务', value: 5, color: '#fa709a' }
])

const quickActions = ref([
  { name: '新增用户', icon: 'Plus', gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', path: '/users' },
  { name: '创建订单', icon: 'DocumentChecked', gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)', path: '/orders' },
  { name: '发布活动', icon: 'Star', gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)', path: '/activities' },
  { name: '系统设置', icon: 'Setting', gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)', path: '/settings' },
  { name: '服务管理', icon: 'Service', gradient: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)', path: '/services' },
  { name: '新闻管理', icon: 'Document', gradient: 'linear-gradient(135deg, #30cfd0 0%, #330867 100%)', path: '/news' }
])

const getStatusType = (status) => {
  const types = ['info', 'warning', 'success', 'success']
  return types[status] || 'info'
}

const getStatusClass = (status) => {
  const classes = ['pending', 'processing', 'completed', 'completed']
  return classes[status] || 'pending'
}

const getStatusIcon = (status) => {
  const icons = ['Timer', 'Loading', 'CircleCheck', 'CircleCheck']
  return icons[status] || 'Timer'
}

const getStatusText = (status) => {
  const texts = ['待处理', '进行中', '已完成', '已取消']
  return texts[status] || '未知'
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  return date.toLocaleDateString()
}

const refreshData = async () => {
  await loadOverview()
  await loadRecentOrders()
  await loadRecentUsers()
  initOrderChart()
  initServiceChart()
}

const loadOverview = async () => {
  try {
    const res = await getOverview()
    statsCards.value[0].value = (res.userCount || 0).toLocaleString()
    statsCards.value[1].value = (res.orderCount || 0).toLocaleString()
    statsCards.value[2].value = (res.serviceCount || 0).toLocaleString()
    statsCards.value[3].value = (res.satisfactionRate || 0) + '%'
  } catch (error) {
    console.error('加载概览数据失败', error)
  }
}

const loadRecentOrders = async () => {
  try {
    const res = await getRecentOrders()
    recentOrders.value = res || []
  } catch (error) {
    console.error('加载最新订单失败', error)
  }
}

const loadRecentUsers = async () => {
  try {
    const res = await getRecentUsers()
    recentUsers.value = res || []
  } catch (error) {
    console.error('加载最新用户失败', error)
  }
}

const initOrderChart = async () => {
  if (!orderChartRef.value) return
  
  if (orderChart) {
    orderChart.dispose()
  }
  
  orderChart = echarts.init(orderChartRef.value)
  
  try {
    const res = await getOrderTrend()
    const days = res?.map(item => item.day) || ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    const orders = res?.map(item => item.orders) || [120, 132, 101, 134, 90, 230, 210]
    const completed = res?.map(item => item.completed) || [100, 110, 90, 120, 80, 200, 180]
    
    const option = {
      tooltip: { 
        trigger: 'axis',
        backgroundColor: 'rgba(255, 255, 255, 0.95)',
        borderColor: '#e2e8f0',
        borderWidth: 1,
        textStyle: { color: '#1e293b' },
        extraCssText: 'box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1); border-radius: 8px;'
      },
      legend: { 
        data: ['订单数', '完成数'],
        bottom: 0,
        icon: 'circle',
        itemGap: 20
      },
      grid: { 
        left: '3%', 
        right: '4%', 
        bottom: '15%', 
        top: '5%',
        containLabel: true 
      },
      xAxis: { 
        type: 'category', 
        boundaryGap: false, 
        data: days,
        axisLine: { lineStyle: { color: '#e2e8f0' } },
        axisLabel: { color: '#64748b' }
      },
      yAxis: { 
        type: 'value',
        axisLine: { show: false },
        axisTick: { show: false },
        splitLine: { lineStyle: { color: '#f1f5f9' } },
        axisLabel: { color: '#64748b' }
      },
      series: [
        {
          name: '订单数',
          type: 'line',
          smooth: true,
          symbol: 'circle',
          symbolSize: 8,
          data: orders,
          itemStyle: { color: '#667eea' },
          lineStyle: { width: 3 },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(102, 126, 234, 0.3)' },
              { offset: 1, color: 'rgba(102, 126, 234, 0.02)' }
            ])
          }
        },
        {
          name: '完成数',
          type: 'line',
          smooth: true,
          symbol: 'circle',
          symbolSize: 8,
          data: completed,
          itemStyle: { color: '#43e97b' },
          lineStyle: { width: 3 },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(67, 233, 123, 0.3)' },
              { offset: 1, color: 'rgba(67, 233, 123, 0.02)' }
            ])
          }
        }
      ]
    }
    orderChart.setOption(option)
  } catch (error) {
    console.error('加载订单趋势失败', error)
  }
}

const initServiceChart = async () => {
  if (!serviceChartRef.value) return
  
  if (serviceChart) {
    serviceChart.dispose()
  }
  
  serviceChart = echarts.init(serviceChartRef.value)
  
  try {
    const res = await getServiceDistribution()
    const data = res || [
      { value: 35, name: '健康服务' },
      { value: 25, name: '家政服务' },
      { value: 20, name: '餐饮服务' },
      { value: 15, name: '陪护服务' },
      { value: 5, name: '其他服务' }
    ]
    
    const option = {
      tooltip: { 
        trigger: 'item',
        backgroundColor: 'rgba(255, 255, 255, 0.95)',
        borderColor: '#e2e8f0',
        borderWidth: 1,
        textStyle: { color: '#1e293b' },
        extraCssText: 'box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1); border-radius: 8px;'
      },
      series: [
        {
          name: '服务类型',
          type: 'pie',
          radius: ['45%', '75%'],
          center: ['50%', '45%'],
          avoidLabelOverlap: false,
          itemStyle: { 
            borderRadius: 10, 
            borderColor: '#fff', 
            borderWidth: 3 
          },
          label: { 
            show: true,
            position: 'outside',
            formatter: '{b}\n{d}%',
            color: '#64748b',
            fontSize: 12
          },
          emphasis: { 
            label: { 
              show: true, 
              fontSize: 14, 
              fontWeight: 'bold' 
            },
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.2)'
            }
          },
          labelLine: { 
            show: true,
            length: 15,
            length2: 10,
            lineStyle: { color: '#e2e8f0' }
          },
          data: data,
          color: ['#667eea', '#f093fb', '#4facfe', '#43e97b', '#fa709a']
        }
      ]
    }
    serviceChart.setOption(option)
  } catch (error) {
    console.error('加载服务分布失败', error)
  }
}

onMounted(() => {
  loadOverview()
  loadRecentOrders()
  loadRecentUsers()
  initOrderChart()
  initServiceChart()
  
  window.addEventListener('resize', () => {
    orderChart?.resize()
    serviceChart?.resize()
  })
})
</script>

<style lang="scss" scoped>
.dashboard {
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

  .header-actions {
    display: flex;
    gap: 12px;

    .refresh-btn {
      border-radius: 10px;
      padding: 10px 20px;
      font-weight: 500;
      
      &:hover {
        background: #f1f5f9;
      }
    }

    .export-btn {
      border-radius: 10px;
      padding: 10px 20px;
      font-weight: 500;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border: none;
      box-shadow: 0 4px 14px rgba(102, 126, 234, 0.4);

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
      }
    }
  }
}

// 统计卡片
.stats-section {
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 20px;
  padding: 24px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.02);
  transition: all 0.3s ease;
  animation: fadeInUp 0.6s ease backwards;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 24px rgba(0, 0, 0, 0.08);
  }

  .stat-bg {
    position: absolute;
    top: 0;
    right: 0;
    width: 150px;
    height: 150px;
    border-radius: 50%;
    opacity: 0.1;
    filter: blur(40px);
    transform: translate(30%, -30%);
  }

  .stat-content {
    position: relative;
    z-index: 1;
    display: flex;
    align-items: flex-start;
    gap: 16px;
  }

  .stat-icon-wrapper {
    width: 56px;
    height: 56px;
    border-radius: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    flex-shrink: 0;
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
  }

  .stat-info {
    flex: 1;

    .stat-value {
      font-size: 32px;
      font-weight: 700;
      color: #1e293b;
      line-height: 1.2;
      margin-bottom: 4px;
    }

    .stat-label {
      font-size: 14px;
      color: #64748b;
      margin-bottom: 8px;
    }

    .stat-trend {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 13px;
      font-weight: 500;

      &.up {
        color: #10b981;
      }

      &.down {
        color: #ef4444;
      }

      .trend-label {
        color: #94a3b8;
        font-weight: 400;
        margin-left: 4px;
      }
    }
  }

  .stat-decoration {
    position: absolute;
    bottom: -20px;
    right: -20px;
    opacity: 0.05;

    .decoration-circle {
      width: 100px;
      height: 100px;
      border: 3px solid #1e293b;
      border-radius: 50%;
    }

    .decoration-ring {
      width: 140px;
      height: 140px;
      border: 2px solid #1e293b;
      border-radius: 50%;
      position: absolute;
      top: -20px;
      left: -20px;
    }
  }
}

// 图表区域
.charts-section {
  margin-bottom: 24px;
}

.chart-card {
  background: #fff;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.02);
  height: 100%;

  &.main-chart {
    min-height: 450px;
  }

  &.side-chart {
    min-height: 450px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .header-left {
      display: flex;
      align-items: center;
      gap: 12px;

      .header-icon {
        width: 44px;
        height: 44px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
      }

      .header-text {
        h3 {
          font-size: 16px;
          font-weight: 600;
          color: #1e293b;
          margin: 0 0 2px;
        }

        p {
          font-size: 13px;
          color: #64748b;
          margin: 0;
        }
      }
    }

    .header-actions {
      :deep(.el-radio-group) {
        .el-radio-button__inner {
          border-radius: 8px;
          padding: 8px 16px;
          font-size: 13px;
        }
      }
    }
  }

  .chart-container {
    height: 320px;
  }

  .chart-legend {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px solid #f1f5f9;

    .legend-item {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 13px;

      .legend-dot {
        width: 10px;
        height: 10px;
        border-radius: 50%;
      }

      .legend-name {
        color: #64748b;
      }

      .legend-value {
        font-weight: 600;
        color: #1e293b;
      }
    }
  }
}

// 列表区域
.lists-section {
  margin-bottom: 24px;
}

.list-card {
  background: #fff;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.02);
  height: 100%;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .header-left {
      display: flex;
      align-items: center;
      gap: 12px;

      .header-icon {
        width: 44px;
        height: 44px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
      }

      .header-text {
        h3 {
          font-size: 16px;
          font-weight: 600;
          color: #1e293b;
          margin: 0 0 2px;
        }

        p {
          font-size: 13px;
          color: #64748b;
          margin: 0;
        }
      }
    }

    .view-all-btn {
      font-weight: 500;
      
      &:hover {
        .el-icon {
          transform: translateX(4px);
        }
      }

      .el-icon {
        transition: transform 0.3s ease;
        margin-left: 4px;
      }
    }
  }

  .list-content {
    .order-item,
    .user-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 14px 0;
      border-bottom: 1px solid #f1f5f9;
      animation: fadeInUp 0.4s ease backwards;
      transition: all 0.3s ease;

      &:hover {
        background: #f8fafc;
        margin: 0 -12px;
        padding-left: 12px;
        padding-right: 12px;
        border-radius: 12px;
      }

      &:last-child {
        border-bottom: none;
      }
    }

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
    }

    .order-info,
    .user-info {
      flex: 1;
      min-width: 0;

      .order-main,
      .user-main {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 4px;

        .order-no {
          font-size: 13px;
          color: #94a3b8;
          font-family: monospace;
        }

        .order-service,
        .user-name {
          font-size: 14px;
          font-weight: 600;
          color: #1e293b;
        }

        .user-age {
          font-size: 12px;
          color: #64748b;
          background: #f1f5f9;
          padding: 2px 8px;
          border-radius: 12px;
        }
      }

      .order-meta,
      .user-meta {
        display: flex;
        align-items: center;
        gap: 12px;
        font-size: 12px;
        color: #64748b;

        .order-user,
        .user-phone {
          display: flex;
          align-items: center;
          gap: 4px;

          .el-icon {
            font-size: 14px;
          }
        }

        .order-time {
          color: #94a3b8;
        }
      }
    }

    .order-status {
      .status-badge {
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
      }
    }

    .user-avatar {
      position: relative;

      .online-indicator {
        position: absolute;
        bottom: 2px;
        right: 2px;
        width: 10px;
        height: 10px;
        background: #10b981;
        border-radius: 50%;
        border: 2px solid #fff;
      }
    }

    .user-time {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 12px;
      color: #94a3b8;
    }
  }

  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 40px;
    color: #94a3b8;

    .el-icon {
      margin-bottom: 12px;
    }

    p {
      margin: 0;
      font-size: 14px;
    }
  }
}

// 快捷入口
.quick-actions {
  .section-title {
    font-size: 18px;
    font-weight: 600;
    color: #1e293b;
    margin: 0 0 16px;
    padding: 0 4px;
  }

  .action-card {
    background: #fff;
    border-radius: 16px;
    padding: 20px;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12px;
    cursor: pointer;
    transition: all 0.3s ease;
    animation: fadeInUp 0.5s ease backwards;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.02);

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 12px 24px rgba(0, 0, 0, 0.08);

      .action-icon {
        transform: scale(1.1);
      }
    }

    .action-icon {
      width: 52px;
      height: 52px;
      border-radius: 14px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      transition: transform 0.3s ease;
      box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
    }

    .action-name {
      font-size: 14px;
      font-weight: 500;
      color: #1e293b;
    }
  }
}

// 动画
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// 响应式
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;

    .header-actions {
      width: 100%;
      
      .el-button {
        flex: 1;
      }
    }
  }

  .stat-card {
    margin-bottom: 16px;
  }

  .chart-card {
    margin-bottom: 16px;
  }
}
</style>