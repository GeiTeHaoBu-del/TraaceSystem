import request from '@/utils/request'

// 分页查询批号列表
export function getBatchPage(params: any) {
  return request({
    url: '/batch/page',
    method: 'get',
    params
  })
}

// 创建养殖批号
export function createBreedingBatch(data: any) {
  return request({
    url: '/batch/breeding',
    method: 'post',
    data
  })
}

// 创建下游批号
export function createDownstreamBatch(data: any) {
  return request({
    url: '/batch/downstream',
    method: 'post',
    data
  })
}

// 发布批号
export function publishBatch(batchId: number) {
  return request({
    url: `/batch/publish/${batchId}`,
    method: 'put'
  })
}

// 批号下架
export function offlineBatch(batchId: number) {
  return request({
    url: `/batch/offline/${batchId}`,
    method: 'put'
  })
}

// 获取可关联的上游批号
export function getAvailableUpstreamBatches(enterpriseType: number) {
  return request({
    url: `/batch/available-upstream/${enterpriseType}`,
    method: 'get'
  })
}

// 溯源查询
export function traceBatch(batchId: number) {
  return request({
    url: `/batch/trace/${batchId}`,
    method: 'get'
  })
}

// 获取批号详情
export function getBatchDetail(batchId: number) {
  return request({
    url: `/batch/${batchId}`,
    method: 'get'
  })
}
