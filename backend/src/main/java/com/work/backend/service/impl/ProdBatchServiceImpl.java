package com.work.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.work.backend.entity.ProdBatch;
import com.work.backend.mapper.ProdBatchMapper;
import com.work.backend.service.ProdBatchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 产品批号服务实现类
 */
@Service
public class ProdBatchServiceImpl extends ServiceImpl<ProdBatchMapper, ProdBatch> implements ProdBatchService {

  @Override
  @Transactional
  public boolean createBreedingBatch(ProdBatch batch) {
    // 验证必须指定下游企业
    if (batch.getDownstreamEnterpriseId() == null) {
      throw new RuntimeException("必须指定下游企业");
    }

    // 生成批号：养殖_日期_序号
    String batchNo = generateBatchNo("养殖", batch.getEnterpriseId());
    batch.setBatchNo(batchNo);
    batch.setBatchStatus(0); // 设置为待发布状态
    batch.setUpstreamBatchId(null); // 养殖企业无上游
    
    // 保存批号（不再自动创建确认请求，由前端手动发布后创建）
    return this.save(batch);
  }

  @Override
  @Transactional
  public boolean createDownstreamBatch(ProdBatch batch) {
    // 这个方法现在不再使用，因为下游批号由确认时自动创建
    throw new RuntimeException("下游批号应由确认请求自动创建，不应手动创建");
  }

  @Override
  public boolean offlineBatch(Long batchId) {
    ProdBatch batch = this.getById(batchId);
    if (batch == null) {
      throw new RuntimeException("批号不存在");
    }

    // 养殖企业：已发布->已下架; 其他：已确认->已下架
    batch.setBatchStatus(batch.getUpstreamBatchId() == null ? 2 : 3);
    batch.setOfflineTime(LocalDateTime.now());
    return this.updateById(batch);
  }

  @Override
  public Page<ProdBatch> pageBatch(int pageNum, int pageSize, Long enterpriseId, Integer batchStatus) {
    Page<ProdBatch> page = new Page<>(pageNum, pageSize);
    LambdaQueryWrapper<ProdBatch> wrapper = new LambdaQueryWrapper<>();

    if (enterpriseId != null) {
      wrapper.eq(ProdBatch::getEnterpriseId, enterpriseId);
    }
    if (batchStatus != null) {
      wrapper.eq(ProdBatch::getBatchStatus, batchStatus);
    }
    wrapper.orderByDesc(ProdBatch::getCreateTime);

    return this.page(page, wrapper);
  }

  @Override
  public List<ProdBatch> getAvailableUpstreamBatches(Long currentEnterpriseId) {
    // 查找指定了当前企业为下游企业的已发布批号，且未被使用的
    LambdaQueryWrapper<ProdBatch> wrapper = new LambdaQueryWrapper<>();

    wrapper.eq(ProdBatch::getBatchStatus, 1) // 已发布
        .eq(ProdBatch::getDownstreamEnterpriseId, currentEnterpriseId); // 指定了当前企业为下游

    // 只返回还没有被创建下游批号的上游批号
    List<ProdBatch> allBatches = this.list(wrapper);
    List<ProdBatch> availableBatches = new ArrayList<>();
    
    for (ProdBatch batch : allBatches) {
      long count = this.count(new LambdaQueryWrapper<ProdBatch>()
          .eq(ProdBatch::getUpstreamBatchId, batch.getBatchId()));
      if (count == 0) {
        availableBatches.add(batch);
      }
    }

    return availableBatches;
  }

  @Override
  public List<ProdBatch> traceBack(Long batchId) {
    List<ProdBatch> traceList = new ArrayList<>();
    ProdBatch currentBatch = this.getById(batchId);

    while (currentBatch != null) {
      traceList.add(currentBatch);
      if (currentBatch.getUpstreamBatchId() == null) {
        break;
      }
      currentBatch = this.getById(currentBatch.getUpstreamBatchId());
    }

    return traceList;
  }

  private String generateBatchNo(String prefix, Long enterpriseId) {
    String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    long count = this.count(new LambdaQueryWrapper<ProdBatch>()
        .likeRight(ProdBatch::getBatchNo, prefix + "_" + date));
    return String.format("%s_%s_%03d", prefix, date, count + 1);
  }

  private String getTypePrefix(ProdBatch batch) {
    // 根据上游批号推断当前企业类型
    ProdBatch upstream = this.getById(batch.getUpstreamBatchId());
    if (upstream.getUpstreamBatchId() == null) {
      return "屠宰"; // 上游是养殖
    } else if (upstream.getBatchNo().startsWith("屠宰")) {
      return "批发";
    } else {
      return "零售";
    }
  }
}
