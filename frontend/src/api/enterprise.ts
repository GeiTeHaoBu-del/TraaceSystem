import request from '@/utils/request'

// 分页查询企业列表
export function getEnterprisePage(params: any) {
  return request({
    url: '/enterprise/page',
    method: 'get',
    params
  })
}

// 获取企业列表（不分页）
export function getEnterpriseList(params?: any) {
  return request({
    url: '/enterprise/list',
    method: 'get',
    params
  })
}

// 创建企业
export function createEnterprise(data: any) {
  return request({
    url: '/enterprise',
    method: 'post',
    data
  })
}

// 获取企业详情
export function getEnterpriseDetail(enterpriseId: number) {
  return request({
    url: `/enterprise/${enterpriseId}`,
    method: 'get'
  })
}

// 更新企业信息
export function updateEnterprise(enterpriseId: number, data: any) {
  return request({
    url: `/enterprise/${enterpriseId}`,
    method: 'put',
    data
  })
}

// 删除企业
export function deleteEnterprise(enterpriseId: number) {
  return request({
    url: `/enterprise/${enterpriseId}`,
    method: 'delete'
  })
}

// 更新企业状态
export function updateEnterpriseStatus(enterpriseId: number, status: number) {
  return request({
    url: `/enterprise/status/${enterpriseId}`,
    method: 'put',
    data: { status }
  })
}

// 获取企业注册统计
export function getEnterpriseStatistics() {
  return request({
    url: '/enterprise/statistics',
    method: 'get'
  })
}
