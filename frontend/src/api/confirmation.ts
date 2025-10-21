import request from '@/utils/request'

// 发起确认请求
export function initiateConfirmation(data: any) {
  return request({
    url: '/confirmation',
    method: 'post',
    data
  })
}

// 确认请求
export function confirmRequest(confirmId: number) {
  return request({
    url: `/confirmation/confirm/${confirmId}`,
    method: 'put'
  })
}

// 创建已确认记录
export function createConfirmedRecord(data: any) {
  return request({
    url: '/confirmation/confirmed',
    method: 'post',
    data
  })
}

// 拒绝请求
export function rejectRequest(confirmId: number, rejectReason: string) {
  return request({
    url: `/confirmation/reject/${confirmId}`,
    method: 'put',
    data: { rejectReason }
  })
}

// 分页查询确认请求
export function getConfirmationPage(params: any) {
  return request({
    url: '/confirmation/page',
    method: 'get',
    params
  })
}
