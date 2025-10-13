package com.work.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.work.backend.entity.BizConfirmation;

/**
 * 确认请求服务接口
 */
public interface BizConfirmationService extends IService<BizConfirmation> {
  /**
   * 发起确认请求
   */
  boolean initiateConfirmation(BizConfirmation confirmation);

  /**
   * 确认请求
   */
  boolean confirmRequest(Long confirmId);

  /**
   * 拒绝请求
   */
  boolean rejectRequest(Long confirmId, String rejectReason);

  /**
   * 分页查询确认请求
   */
  Page<BizConfirmation> pageConfirmation(int pageNum, int pageSize, Long enterpriseId, Integer confirmStatus,
      String type);
}
