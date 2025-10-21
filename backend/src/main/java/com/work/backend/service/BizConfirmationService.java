package com.work.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.work.backend.entity.BizConfirmation;
import com.work.backend.vo.ConfirmationVO;

/**
 * 确认请求服务接口
 */
public interface BizConfirmationService extends IService<BizConfirmation> {
  /**
   * 发起确认请求
   */
  boolean initiateConfirmation(BizConfirmation confirmation);

  /**
   * 确认请求（并指定下游企业）
   */
  boolean confirmRequest(Long confirmId, Long downstreamEnterpriseId);

  /**
   * 创建已确认的确认记录
   */
  boolean createConfirmedRecord(Long initiateEnterpriseId, Long batchId, Long receiveEnterpriseId);

  /**
   * 拒绝请求
   */
  boolean rejectRequest(Long confirmId, String rejectReason);

  /**
   * 分页查询确认请求
   */
  Page<BizConfirmation> pageConfirmation(int pageNum, int pageSize, Long enterpriseId, Integer confirmStatus,
      String type);
      
  /**
   * 分页查询确认请求详情(带批号信息)
   */
  Page<ConfirmationVO> pageConfirmationWithBatch(int pageNum, int pageSize, 
                                                  Long receiveEnterpriseId, 
                                                  Long initiateEnterpriseId,
                                                  Integer confirmStatus);
}
