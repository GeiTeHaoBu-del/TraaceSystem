package com.work.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.work.backend.entity.ProdBatch;
import com.work.backend.mapper.ProdBatchMapper;
import com.work.backend.service.ProdBatchService;
import org.springframework.stereotype.Service;

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
  public boolean createBreedingBatch(ProdBatch batch) {
    // 生成批号：养殖_日期_序号
    String batchNo = generateBatchNo("养殖", batch.getEnterpriseId());
    batch.setBatchNo(batchNo);
    batch.setBatchStatus(0); // 待发布
    batch.setUpstreamBatchId(null); // 养殖企业无上游
    return this.save(batch);
  }

  @Override
  public boolean createDownstreamBatch(ProdBatch batch) {
    // 验证上游批号是否存在且已确认
    if (batch.getUpstreamBatchId() == null) {
      throw new RuntimeException("必须关联上游批号");
    }

    ProdBatch upstreamBatch = this.getById(batch.getUpstreamBatchId());
    if (upstreamBatch == null) {
      throw new RuntimeException("上游批号不存在");
    }

    // 生成批号
    String typePrefix = getTypePrefix(batch);
    String batchNo = generateBatchNo(typePrefix, batch.getEnterpriseId());
    batch.setBatchNo(batchNo);
    batch.setBatchStatus(0); // 新建

    return this.save(batch);
  }

  @Override
  public boolean publishBreedingBatch(Long batchId) {
    ProdBatch batch = this.getById(batchId);
    if (batch == null) {
      throw new RuntimeException("批号不存在");
    }
    if (batch.getBatchStatus() != 0) {
      throw new RuntimeException("只能发布待发布状态的批号");
    }

    batch.setBatchStatus(1); // 已发布
    return this.updateById(batch);
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
  public List<ProdBatch> getAvailableUpstreamBatches(Integer enterpriseType) {
    // 根据企业类型获取可关联的上游批号
    // 屠宰->养殖(已发布), 批发->屠宰(已确认), 零售->批发(已确认)
    LambdaQueryWrapper<ProdBatch> wrapper = new LambdaQueryWrapper<>();

    if (enterpriseType == 2) { // 屠宰
      wrapper.eq(ProdBatch::getBatchStatus, 1) // 已发布
          .isNull(ProdBatch::getUpstreamBatchId); // 养殖批号
    } else if (enterpriseType == 3 || enterpriseType == 4) { // 批发或零售
      wrapper.eq(ProdBatch::getBatchStatus, 2); // 已确认
    }

    wrapper.orderByDesc(ProdBatch::getCreateTime);
    return this.list(wrapper);
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
