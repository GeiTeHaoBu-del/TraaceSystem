package com.work.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.work.backend.entity.BizConfirmation;
import com.work.backend.mapper.BizConfirmationMapper;
import com.work.backend.service.BizConfirmationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 确认请求服务实现类
 */
@Service
public class BizConfirmationServiceImpl extends ServiceImpl<BizConfirmationMapper, BizConfirmation>
    implements BizConfirmationService {

  @Override
  public boolean createConfirmedRecord(Long initiateEnterpriseId, Long batchId, Long receiveEnterpriseId) {
    BizConfirmation confirmation = new BizConfirmation();
    confirmation.setInitiateEnterpriseId(initiateEnterpriseId);
    confirmation.setBatchId(batchId);
    confirmation.setReceiveEnterpriseId(receiveEnterpriseId);
    confirmation.setConfirmStatus(1); // 已确认
    confirmation.setInitiateTime(LocalDateTime.now());
    confirmation.setConfirmTime(LocalDateTime.now());
    return this.save(confirmation);
  }

  @Override
  public boolean initiateConfirmation(BizConfirmation confirmation) {
    confirmation.setConfirmStatus(0); // 待确认
    confirmation.setInitiateTime(LocalDateTime.now());
    return this.save(confirmation);
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
    return this.updateById(confirmation);
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
    return this.updateById(confirmation);
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
