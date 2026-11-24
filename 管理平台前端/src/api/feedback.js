import request from '@/utils/request'

export function getFeedbackList(params) {
  return request({
    url: '/admin/feedback/list',
    method: 'get',
    params
  })
}

export function getFeedbackDetail(id) {
  return request({
    url: `/admin/feedback/${id}`,
    method: 'get'
  })
}

export function replyFeedback(id, reply) {
  return request({
    url: `/admin/feedback/${id}/reply`,
    method: 'put',
    data: { reply }
  })
}
