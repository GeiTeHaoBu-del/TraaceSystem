package com.work.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.work.backend.entity.BizConfirmation;
import com.work.backend.entity.ProdBatch;
import com.work.backend.mapper.BizConfirmationMapper;
import com.work.backend.service.BizConfirmationService;
import com.work.backend.service.ProdBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 确认请求服务实现类
 */
@Service
public class BizConfirmationServiceImpl extends ServiceImpl<BizConfirmationMapper, BizConfirmation>
    implements BizConfirmationService {

  @Autowired
  private ProdBatchService prodBatchService;

  @Override
  public boolean initiateConfirmation(BizConfirmation confirmation) {
    // 验证批号
    ProdBatch batch = prodBatchService.getById(confirmation.getBatchId());
    if (batch == null) {
      throw new RuntimeException("批号不存在");
    }
    if (batch.getBatchStatus() != 0) {
      throw new RuntimeException("批号状态不是新建状态");
    }

    // 获取上游企业ID
    ProdBatch upstreamBatch = prodBatchService.getById(batch.getUpstreamBatchId());
    confirmation.setReceiveEnterpriseId(upstreamBatch.getEnterpriseId());
    confirmation.setConfirmStatus(0); // 待确认
    confirmation.setInitiateTime(LocalDateTime.now());

    boolean result = this.save(confirmation);
    if (result) {
      // 更新批号状态为待确认
      batch.setBatchStatus(1);
      prodBatchService.updateById(batch);
    }

    return result;
  }

  @Override
  @Transactional
  public boolean confirmRequest(Long confirmId) {
    BizConfirmation confirmation = this.getById(confirmId);
    if (confirmation == null) {
      throw new RuntimeException("确认请求不存在");
    }
    if (confirmation.getConfirmStatus() != 0) {
      throw new RuntimeException("请求已处理");
    }

    confirmation.setConfirmStatus(1); // 已确认
    confirmation.setConfirmTime(LocalDateTime.now());
    boolean result = this.updateById(confirmation);

    if (result) {
      // 更新批号状态为已确认
      ProdBatch batch = prodBatchService.getById(confirmation.getBatchId());
      batch.setBatchStatus(2);
      prodBatchService.updateById(batch);
    }

    return result;
  }

  @Override
  @Transactional
  public boolean rejectRequest(Long confirmId, String rejectReason) {
    BizConfirmation confirmation = this.getById(confirmId);
    if (confirmation == null) {
      throw new RuntimeException("确认请求不存在");
    }
    if (confirmation.getConfirmStatus() != 0) {
      throw new RuntimeException("请求已处理");
    }

    confirmation.setConfirmStatus(2); // 已拒绝
    confirmation.setConfirmTime(LocalDateTime.now());
    confirmation.setRejectReason(rejectReason);
    boolean result = this.updateById(confirmation);

    if (result) {
      // 批号状态回退为新建
      ProdBatch batch = prodBatchService.getById(confirmation.getBatchId());
      batch.setBatchStatus(0);
      prodBatchService.updateById(batch);
    }

    return result;
  }

  @Override
  public Page<BizConfirmation> pageConfirmation(int pageNum, int pageSize, Long enterpriseId,
      Integer confirmStatus, String type) {
    Page<BizConfirmation> page = new Page<>(pageNum, pageSize);
    LambdaQueryWrapper<BizConfirmation> wrapper = new LambdaQueryWrapper<>();

    if (enterpriseId != null) {
      if ("initiate".equals(type)) {
        wrapper.eq(BizConfirmation::getInitiateEnterpriseId, enterpriseId);
      } else if ("receive".equals(type)) {
        wrapper.eq(BizConfirmation::getReceiveEnterpriseId, enterpriseId);
      }
    }

    if (confirmStatus != null) {
      wrapper.eq(BizConfirmation::getConfirmStatus, confirmStatus);
    }

    wrapper.orderByDesc(BizConfirmation::getInitiateTime);
    return this.page(page, wrapper);
  }
}
