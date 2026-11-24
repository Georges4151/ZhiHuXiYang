import request from '@/utils/request'

export function getEmergencyList(params) {
  return request({
    url: '/admin/emergency/list',
    method: 'get',
    params
  })
}

export function getEmergencyDetail(id) {
  return request({
    url: `/admin/emergency/${id}`,
    method: 'get'
  })
}

export function updateEmergencyStatus(id, callStatus) {
  return request({
    url: `/admin/emergency/${id}/status`,
    method: 'put',
    data: { callStatus }
  })
}

export function getEmergencyStats() {
  return request({
    url: '/admin/emergency/stats',
    method: 'get'
  })
}
