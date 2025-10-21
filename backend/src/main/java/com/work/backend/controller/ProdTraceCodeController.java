package com.work.backend.controller;

import com.work.backend.common.Result;
import com.work.backend.entity.ProdTraceCode;
import com.work.backend.entity.ProdBatch;
import com.work.backend.service.ProdTraceCodeService;
import com.work.backend.service.ProdBatchService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 溯源码控制器
 */
@RestController
@RequestMapping("/api/trace-code")
public class ProdTraceCodeController {

  @Autowired
  private ProdTraceCodeService prodTraceCodeService;

  @Autowired
  private ProdBatchService prodBatchService;

  /**
   * 生成溯源码
   */
  @PostMapping("/generate/{batchId}")
  public Result<Map<String, String>> generateTraceCode(@PathVariable Long batchId) {
    String traceCode = prodTraceCodeService.generateTraceCode(batchId);
    Map<String, String> result = new HashMap<>();
    result.put("traceCode", traceCode);
    return Result.success(result);
  }

  /**
   * 根据溯源码查询完整溯源信息
   */
  @GetMapping("/trace/{traceCode}")
  public Result<List<Object>> getTraceInfo(@PathVariable String traceCode) {
    List<Object> traceInfo = prodTraceCodeService.getTraceInfo(traceCode);
    return Result.success(traceInfo);
  }

  /**
   * 批量生成溯源码
   */
  @PostMapping("/batch-generate")
  public Result<List<String>> batchGenerate(@RequestBody Map<String, Object> data) {
    @SuppressWarnings("unchecked")
    List<Long> batchIds = (List<Long>) data.get("batchIds");

    List<String> traceCodes = new java.util.ArrayList<>();
    for (Long batchId : batchIds) {
      String traceCode = prodTraceCodeService.generateTraceCode(batchId);
      traceCodes.add(traceCode);
    }

    return Result.success(traceCodes);
  }

  /**
   * 分页查询溯源码列表（用于管理后台统计/分页）
   */
  @GetMapping("/page")
  public Result<Page<ProdTraceCode>> getPage(
      @RequestParam(defaultValue = "1") Integer pageNum,
      @RequestParam(defaultValue = "10") Integer pageSize,
      @RequestParam(required = false) Long enterpriseId) {

    Page<ProdTraceCode> page = new Page<>(pageNum, pageSize);
    LambdaQueryWrapper<ProdTraceCode> wrapper = new LambdaQueryWrapper<>();

    if (enterpriseId != null) {
      // 根据企业ID查询其所有批号ID
      LambdaQueryWrapper<ProdBatch> batchWrapper = new LambdaQueryWrapper<>();
      batchWrapper.eq(ProdBatch::getEnterpriseId, enterpriseId)
          .select(ProdBatch::getBatchId);
      List<ProdBatch> batches = prodBatchService.list(batchWrapper);
      List<Long> batchIds = batches.stream()
          .map(ProdBatch::getBatchId)
          .collect(Collectors.toList());
      
      if (!batchIds.isEmpty()) {
        wrapper.in(ProdTraceCode::getBatchId, batchIds);
      }
    }

    wrapper.orderByDesc(ProdTraceCode::getGenerateTime);
    Page<ProdTraceCode> result = prodTraceCodeService.page(page, wrapper);

    // 加载每个溯源码对应的批号信息
    for (ProdTraceCode traceCode : result.getRecords()) {
      ProdBatch batch = prodBatchService.getById(traceCode.getBatchId());
      if (batch != null) {
        traceCode.setBatchNo(batch.getBatchNo());
      }
    }

    return Result.success(result);
  }
}
