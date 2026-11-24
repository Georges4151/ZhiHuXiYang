import request from '@/utils/request'

export function getUserList(params) {
  return request({
    url: '/admin/user/list',
    method: 'get',
    params
  })
}

export function createUser(data) {
  return request({
    url: '/admin/user',
    method: 'post',
    data
  })
}

export function getUserDetail(id) {
  return request({
    url: `/admin/user/${id}`,
    method: 'get'
  })
}

export function updateUser(id, data) {
  return request({
    url: `/admin/user/${id}`,
    method: 'put',
    data
  })
}

export function deleteUser(id) {
  return request({
    url: `/admin/user/${id}`,
    method: 'delete'
  })
}

export function getUserStats() {
  return request({
    url: '/admin/user/stats',
    method: 'get'
  })
}
