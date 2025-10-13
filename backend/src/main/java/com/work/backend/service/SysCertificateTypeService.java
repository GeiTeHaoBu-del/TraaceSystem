package com.work.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.work.backend.entity.SysCertificateType;
import java.util.List;

/**
 * 证件类型服务接口
 */
public interface SysCertificateTypeService extends IService<SysCertificateType> {
  /**
   * 根据企业类型获取适用证件类型
   */
  List<SysCertificateType> getByEnterpriseType(Integer enterpriseType);
}
