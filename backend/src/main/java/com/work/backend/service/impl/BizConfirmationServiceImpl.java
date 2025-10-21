package com.work.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.work.backend.entity.BizConfirmation;
import com.work.backend.entity.ProdBatch;
import com.work.backend.mapper.BizConfirmationMapper;
import com.work.backend.service.BizConfirmationService;
import com.work.backend.service.ProdBatchService;
import com.work.backend.vo.ConfirmationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 确认请求服务实现类
 */
@Service
public class BizConfirmationServiceImpl extends ServiceImpl<BizConfirmationMapper, BizConfirmation>
    implements BizConfirmationService {

  @Autowired
  private ProdBatchService prodBatchService;

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
  public boolean confirmRequest(Long confirmId, Long downstreamEnterpriseId) {
    BizConfirmation confirmation = this.getById(confirmId);
    if (confirmation == null) {
      throw new RuntimeException("确认请求不存在");
    }
    if (confirmation.getConfirmStatus() != 0) {
      throw new RuntimeException("请求已处理");
    }

    // 获取上游批号信息
    ProdBatch upstreamBatch = prodBatchService.getById(confirmation.getBatchId());
    if (upstreamBatch == null) {
      throw new RuntimeException("上游批号不存在");
    }

    // 更新确认状态
    confirmation.setConfirmStatus(1); // 已确认
    confirmation.setConfirmTime(LocalDateTime.now());
    boolean updated = this.updateById(confirmation);

    if (updated) {
      // 自动创建下游批号
      ProdBatch downstreamBatch = new ProdBatch();
      downstreamBatch.setEnterpriseId(confirmation.getReceiveEnterpriseId());
      downstreamBatch.setUpstreamBatchId(upstreamBatch.getBatchId());
      downstreamBatch.setProductVariety(upstreamBatch.getProductVariety());
      downstreamBatch.setCertNo(generateCertNo(confirmation.getReceiveEnterpriseId()));
      
      // 生成批号
      String typePrefix = getTypePrefixByUpstream(upstreamBatch);
      String batchNo = generateBatchNo(typePrefix, confirmation.getReceiveEnterpriseId());
      downstreamBatch.setBatchNo(batchNo);
      downstreamBatch.setBatchStatus(1); // 已发布
      
      // 设置下游企业（非零售企业需要指定）
      if (!"零售".equals(typePrefix)) {
        if (downstreamEnterpriseId == null) {
          throw new RuntimeException("必须指定下游企业");
        }
        downstreamBatch.setDownstreamEnterpriseId(downstreamEnterpriseId);
      } else {
        // 零售企业不需要下游企业
        downstreamBatch.setDownstreamEnterpriseId(null);
      }
      
      // 保存下游批号
      prodBatchService.save(downstreamBatch);
      
      // 如果指定了下游企业，自动创建下一级的确认请求
      if (downstreamEnterpriseId != null && !"零售".equals(typePrefix)) {
        BizConfirmation nextConfirmation = new BizConfirmation();
        nextConfirmation.setInitiateEnterpriseId(confirmation.getReceiveEnterpriseId());
        nextConfirmation.setBatchId(downstreamBatch.getBatchId());
        nextConfirmation.setReceiveEnterpriseId(downstreamEnterpriseId);
        nextConfirmation.setConfirmStatus(0); // 待确认
        nextConfirmation.setInitiateTime(LocalDateTime.now());
        this.save(nextConfirmation);
      }
    }

    return updated;
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

  @Override
  public Page<ConfirmationVO> pageConfirmationWithBatch(int pageNum, int pageSize, 
                                                         Long receiveEnterpriseId, 
                                                         Long initiateEnterpriseId,
                                                         Integer confirmStatus) {
    Page<ConfirmationVO> page = new Page<>(pageNum, pageSize);
    return this.baseMapper.selectConfirmationPage(page, receiveEnterpriseId, initiateEnterpriseId, confirmStatus);
  }

  /**
   * 根据上游批号推断当前企业类型前缀
   */
  private String getTypePrefixByUpstream(ProdBatch upstreamBatch) {
    if (upstreamBatch.getUpstreamBatchId() == null) {
      return "屠宰"; // 上游是养殖
    } else if (upstreamBatch.getBatchNo().startsWith("屠宰")) {
      return "批发";
    } else {
      return "零售";
    }
  }

  /**
   * 生成批号
   */
  private String generateBatchNo(String prefix, Long enterpriseId) {
    String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    long count = prodBatchService.count(new LambdaQueryWrapper<ProdBatch>()
        .likeRight(ProdBatch::getBatchNo, prefix + "_" + date));
    return String.format("%s_%s_%03d", prefix, date, count + 1);
  }

  /**
   * 生成证件编号（示例）
   */
  private String generateCertNo(Long enterpriseId) {
    return "CERT_" + enterpriseId + "_" + System.currentTimeMillis();
  }
}
