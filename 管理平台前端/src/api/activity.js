import request from '@/utils/request'

export function getActivityList(params) {
  return request({
    url: '/admin/activity/list',
    method: 'get',
    params
  })
}

export function getActivityDetail(id) {
  return request({
    url: `/admin/activity/${id}`,
    method: 'get'
  })
}

export function addActivity(data) {
  return request({
    url: '/admin/activity',
    method: 'post',
    data
  })
}

export function updateActivity(id, data) {
  return request({
    url: `/admin/activity/${id}`,
    method: 'put',
    data
  })
}

export function deleteActivity(id) {
  return request({
    url: `/admin/activity/${id}`,
    method: 'delete'
  })
}
