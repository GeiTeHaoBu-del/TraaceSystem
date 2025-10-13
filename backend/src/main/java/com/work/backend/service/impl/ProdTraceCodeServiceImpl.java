package com.work.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.work.backend.entity.ProdBatch;
import com.work.backend.entity.ProdTraceCode;
import com.work.backend.mapper.ProdTraceCodeMapper;
import com.work.backend.service.ProdBatchService;
import com.work.backend.service.ProdTraceCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 溯源码服务实现类
 */
@Service
public class ProdTraceCodeServiceImpl extends ServiceImpl<ProdTraceCodeMapper, ProdTraceCode>
    implements ProdTraceCodeService {

  @Autowired
  private ProdBatchService prodBatchService;

  @Override
  public String generateTraceCode(Long batchId) {
    // 验证批号
    ProdBatch batch = prodBatchService.getById(batchId);
    if (batch == null) {
      throw new RuntimeException("批号不存在");
    }
    if (batch.getBatchStatus() != 2) {
      throw new RuntimeException("只能为已确认的批号生成溯源码");
    }

    // 检查是否已生成溯源码
    ProdTraceCode existCode = this.lambdaQuery()
        .eq(ProdTraceCode::getBatchId, batchId)
        .one();
    if (existCode != null) {
      return existCode.getTraceCode();
    }

    // 生成UUID作为溯源码
    String traceCode = UUID.randomUUID().toString().replace("-", "");

    ProdTraceCode prodTraceCode = new ProdTraceCode();
    prodTraceCode.setTraceCode(traceCode);
    prodTraceCode.setBatchId(batchId);
    prodTraceCode.setGenerateTime(LocalDateTime.now());
    prodTraceCode.setStatus(1);

    this.save(prodTraceCode);
    return traceCode;
  }

  @Override
  public List<Object> getTraceInfo(String traceCode) {
    // 根据溯源码查询批号
    ProdTraceCode prodTraceCode = this.lambdaQuery()
        .eq(ProdTraceCode::getTraceCode, traceCode)
        .one();

    if (prodTraceCode == null) {
      throw new RuntimeException("溯源码不存在");
    }

    // 溯源整个链路
    List<ProdBatch> traceList = prodBatchService.traceBack(prodTraceCode.getBatchId());

    return new ArrayList<>(traceList);
  }
}
