package com.work.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.work.backend.entity.SysEnterpriseCertificate;
import java.util.List;

/**
 * 企业证件服务接口
 */
public interface SysEnterpriseCertificateService extends IService<SysEnterpriseCertificate> {
  /**
   * 获取企业所有证件
   */
  List<SysEnterpriseCertificate> getByEnterpriseId(Long enterpriseId);

  /**
   * 检查证件是否过期
   */
  void checkAndUpdateExpiredCertificates();
}
