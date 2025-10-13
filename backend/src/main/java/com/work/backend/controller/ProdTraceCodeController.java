package com.work.backend.controller;

import com.work.backend.common.Result;
import com.work.backend.service.ProdTraceCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 溯源码控制器
 */
@RestController
@RequestMapping("/api/trace-code")
public class ProdTraceCodeController {

  @Autowired
  private ProdTraceCodeService prodTraceCodeService;

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
}
