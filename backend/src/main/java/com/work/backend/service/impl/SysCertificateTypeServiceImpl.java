package com.work.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.work.backend.entity.SysCertificateType;
import com.work.backend.entity.SysCertTypeApplyEnterprise;
import com.work.backend.mapper.SysCertificateTypeMapper;
import com.work.backend.mapper.SysCertTypeApplyEnterpriseMapper;
import com.work.backend.service.SysCertificateTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 证件类型服务实现类
 */
@Service
public class SysCertificateTypeServiceImpl extends ServiceImpl<SysCertificateTypeMapper, SysCertificateType>
    implements SysCertificateTypeService {

  @Autowired
  private SysCertTypeApplyEnterpriseMapper certTypeApplyEnterpriseMapper;

  @Override
  public List<SysCertificateType> getByEnterpriseType(Integer enterpriseType) {
    // 查询适用于该企业类型的证件类型ID列表
    List<SysCertTypeApplyEnterprise> applyList = certTypeApplyEnterpriseMapper.selectList(
        new LambdaQueryWrapper<SysCertTypeApplyEnterprise>()
            .eq(SysCertTypeApplyEnterprise::getApplyEnterpriseType, enterpriseType));

    if (applyList.isEmpty()) {
      return List.of();
    }

    List<Integer> certTypeIds = applyList.stream()
        .map(SysCertTypeApplyEnterprise::getCertTypeId)
        .collect(Collectors.toList());

    // 查询证件类型详情
    return this.listByIds(certTypeIds);
  }
}
