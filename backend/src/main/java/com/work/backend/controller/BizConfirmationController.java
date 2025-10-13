package com.work.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.work.backend.common.Result;
import com.work.backend.entity.BizConfirmation;
import com.work.backend.service.BizConfirmationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 确认请求控制器
 */
@RestController
@RequestMapping("/api/confirmation")
public class BizConfirmationController {

  @Autowired
  private BizConfirmationService bizConfirmationService;

  /**
   * 发起确认请求
   */
  @PostMapping
  public Result<String> initiateConfirmation(@RequestBody BizConfirmation confirmation) {
    boolean success = bizConfirmationService.initiateConfirmation(confirmation);
    return success ? Result.success("请求发起成功") : Result.error("请求发起失败");
  }

  /**
   * 确认请求
   */
  @PutMapping("/confirm/{confirmId}")
  public Result<String> confirmRequest(@PathVariable Long confirmId) {
    boolean success = bizConfirmationService.confirmRequest(confirmId);
    return success ? Result.success("确认成功") : Result.error("确认失败");
  }

  /**
   * 拒绝请求
   */
  @PutMapping("/reject/{confirmId}")
  public Result<String> rejectRequest(@PathVariable Long confirmId, @RequestBody Map<String, String> data) {
    String rejectReason = data.get("rejectReason");
    boolean success = bizConfirmationService.rejectRequest(confirmId, rejectReason);
    return success ? Result.success("拒绝成功") : Result.error("拒绝失败");
  }

  /**
   * 分页查询确认请求列表
   */
  @GetMapping("/page")
  public Result<Page<BizConfirmation>> getPage(
      @RequestParam(defaultValue = "1") Integer pageNum,
      @RequestParam(defaultValue = "10") Integer pageSize,
      @RequestParam(required = false) Long enterpriseId,
      @RequestParam(required = false) Integer confirmStatus,
      @RequestParam(required = false) String type) {

    Page<BizConfirmation> page = bizConfirmationService.pageConfirmation(
        pageNum, pageSize, enterpriseId, confirmStatus, type);
    return Result.success(page);
  }

  /**
   * 获取确认请求详情
   */
  @GetMapping("/{confirmId}")
  public Result<BizConfirmation> getById(@PathVariable Long confirmId) {
    BizConfirmation confirmation = bizConfirmationService.getById(confirmId);
    return Result.success(confirmation);
  }
}
