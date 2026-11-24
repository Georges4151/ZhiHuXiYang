import request from '@/utils/request'

export function getServiceList(params) {
  return request({
    url: '/admin/service/list',
    method: 'get',
    params
  })
}

export function getServiceDetail(id) {
  return request({
    url: `/admin/service/${id}`,
    method: 'get'
  })
}

export function addService(data) {
  return request({
    url: '/admin/service',
    method: 'post',
    data
  })
}

export function updateService(id, data) {
  return request({
    url: `/admin/service/${id}`,
    method: 'put',
    data
  })
}

export function deleteService(id) {
  return request({
    url: `/admin/service/${id}`,
    method: 'delete'
  })
}
