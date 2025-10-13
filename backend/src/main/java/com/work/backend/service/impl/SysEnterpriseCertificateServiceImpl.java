package com.work.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.work.backend.entity.SysEnterpriseCertificate;
import com.work.backend.mapper.SysEnterpriseCertificateMapper;
import com.work.backend.service.SysEnterpriseCertificateService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 企业证件服务实现类
 */
@Service
public class SysEnterpriseCertificateServiceImpl
    extends ServiceImpl<SysEnterpriseCertificateMapper, SysEnterpriseCertificate>
    implements SysEnterpriseCertificateService {

  @Override
  public List<SysEnterpriseCertificate> getByEnterpriseId(Long enterpriseId) {
    return this.lambdaQuery()
        .eq(SysEnterpriseCertificate::getEnterpriseId, enterpriseId)
        .orderByDesc(SysEnterpriseCertificate::getCreateTime)
        .list();
  }

  @Override
  public void checkAndUpdateExpiredCertificates() {
    // 将过期的证件状态设置为无效
    this.update(new LambdaUpdateWrapper<SysEnterpriseCertificate>()
        .set(SysEnterpriseCertificate::getStatus, 0)
        .lt(SysEnterpriseCertificate::getValidUntil, LocalDate.now())
        .eq(SysEnterpriseCertificate::getStatus, 1));
  }
}
