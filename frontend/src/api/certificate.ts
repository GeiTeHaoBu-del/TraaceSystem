import request from '@/utils/request'

// 获取所有证件类型
export function getCertificateTypeList() {
  return request({
    url: '/certificate-type/list',
    method: 'get'
  })
}

// 根据企业类型获取适用证件类型
export function getCertificateTypeByEnterpriseType(enterpriseType: number) {
  return request({
    url: `/certificate-type/by-enterprise-type/${enterpriseType}`,
    method: 'get'
  })
}

// 获取企业所有证件
export function getEnterpriseCertificates(enterpriseId: number) {
  return request({
    url: `/enterprise-certificate/enterprise/${enterpriseId}`,
    method: 'get'
  })
}

// 添加企业证件
export function addEnterpriseCertificate(data: any) {
  return request({
    url: '/enterprise-certificate',
    method: 'post',
    data
  })
}

// 更新企业证件
export function updateEnterpriseCertificate(certId: number, data: any) {
  return request({
    url: `/enterprise-certificate/${certId}`,
    method: 'put',
    data
  })
}

// 删除企业证件
export function deleteEnterpriseCertificate(certId: number) {
  return request({
    url: `/enterprise-certificate/${certId}`,
    method: 'delete'
  })
}
