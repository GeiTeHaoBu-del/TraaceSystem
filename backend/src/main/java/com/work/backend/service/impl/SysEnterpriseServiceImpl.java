package com.work.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.work.backend.entity.SysEnterprise;
import com.work.backend.mapper.SysEnterpriseMapper;
import com.work.backend.service.SysEnterpriseService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 企业服务实现类
 */
@Service
public class SysEnterpriseServiceImpl extends ServiceImpl<SysEnterpriseMapper, SysEnterprise>
    implements SysEnterpriseService {

  @Override
  public Page<SysEnterprise> pageEnterprise(int pageNum, int pageSize, Integer enterpriseType, String province) {
    Page<SysEnterprise> page = new Page<>(pageNum, pageSize);
    LambdaQueryWrapper<SysEnterprise> wrapper = new LambdaQueryWrapper<>();

    if (enterpriseType != null) {
      wrapper.eq(SysEnterprise::getEnterpriseType, enterpriseType);
    }
    if (province != null && !province.isEmpty()) {
      wrapper.eq(SysEnterprise::getProvince, province);
    }
    wrapper.orderByDesc(SysEnterprise::getCreateTime);

    return this.page(page, wrapper);
  }

  @Override
  public Map<String, Object> getRegisterStatistics() {
    List<SysEnterprise> allEnterprises = this.list();

    // 按省份统计
    Map<String, Long> provinceStats = allEnterprises.stream()
        .collect(Collectors.groupingBy(SysEnterprise::getProvince, Collectors.counting()));

    // 按月份统计
    Map<String, Long> monthStats = allEnterprises.stream()
        .collect(Collectors.groupingBy(
            e -> e.getRegisterTime().getYear() + "-" +
                String.format("%02d", e.getRegisterTime().getMonthValue()),
            Collectors.counting()));

    // 按企业类型统计
    Map<Integer, Long> typeStats = allEnterprises.stream()
        .collect(Collectors.groupingBy(SysEnterprise::getEnterpriseType, Collectors.counting()));

    Map<String, Object> result = new HashMap<>();
    result.put("provinceStats", provinceStats);
    result.put("monthStats", monthStats);
    result.put("typeStats", typeStats);
    result.put("total", allEnterprises.size());

    return result;
  }
}
