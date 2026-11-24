import request from '@/utils/request'

export function getHealthList(params) {
  return request({
    url: '/admin/health/list',
    method: 'get',
    params
  })
}

export function getHealthStats() {
  return request({
    url: '/admin/health/stats',
    method: 'get'
  })
}
