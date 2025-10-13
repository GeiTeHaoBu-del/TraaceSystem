package com.work.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.work.backend.entity.SysEnterprise;
import java.util.Map;

/**
 * 企业服务接口
 */
public interface SysEnterpriseService extends IService<SysEnterprise> {
  /**
   * 分页查询企业
   */
  Page<SysEnterprise> pageEnterprise(int pageNum, int pageSize, Integer enterpriseType, String province);

  /**
   * 企业注册统计
   */
  Map<String, Object> getRegisterStatistics();
}
