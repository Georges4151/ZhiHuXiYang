import request from '@/utils/request'

export function getOrderList(params) {
  return request({
    url: '/admin/order/list',
    method: 'get',
    params
  })
}

export function getOrderDetail(id) {
  return request({
    url: `/admin/order/${id}`,
    method: 'get'
  })
}

export function updateOrderStatus(id, status) {
  return request({
    url: `/admin/order/${id}/status`,
    method: 'put',
    data: { status }
  })
}

export function getOrderStats() {
  return request({
    url: '/admin/order/stats',
    method: 'get'
  })
}
