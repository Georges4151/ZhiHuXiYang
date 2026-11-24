import request from '@/utils/request'

export function getOverview() {
  return request({
    url: '/admin/stats/overview',
    method: 'get'
  })
}

export function getOrderTrend(type) {
  return request({
    url: '/admin/stats/order-trend',
    method: 'get',
    params: { type }
  })
}

export function getServiceDistribution() {
  return request({
    url: '/admin/stats/service-distribution',
    method: 'get'
  })
}

export function getRecentOrders() {
  return request({
    url: '/admin/stats/recent-orders',
    method: 'get'
  })
}

export function getRecentUsers() {
  return request({
    url: '/admin/stats/recent-users',
    method: 'get'
  })
}
