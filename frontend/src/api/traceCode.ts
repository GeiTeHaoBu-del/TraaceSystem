import request from '@/utils/request'

// 分页查询溯源码
export function getTraceCodePage(params: any) {
  return request({
    url: '/trace-code/page',
    method: 'get',
    params
  })
}

// 生成溯源码
export function generateTraceCode(batchId: number) {
  return request({
    url: `/trace-code/generate/${batchId}`,
    method: 'post'
  })
}

// 根据批号ID获取溯源码列表
export function getTraceCodeByBatchId(batchId: number) {
  return request({
    url: `/trace-code/batch/${batchId}`,
    method: 'get'
  })
}

// 根据溯源码查询溯源信息
export function getTraceInfo(traceCode: string) {
  return request({
    url: `/trace-code/trace/${traceCode}`,
    method: 'get'
  })
}
