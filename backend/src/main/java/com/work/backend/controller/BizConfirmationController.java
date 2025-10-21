package com.work.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.work.backend.common.Result;
import com.work.backend.entity.BizConfirmation;
import com.work.backend.service.BizConfirmationService;
import com.work.backend.vo.ConfirmationVO;
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
   * 创建已确认的确认记录
   */
  @PostMapping("/confirmed")
  public Result<String> createConfirmedRecord(@RequestBody Map<String, Object> params) {
    Long initiateEnterpriseId = Long.valueOf(params.get("initiateEnterpriseId").toString());
    Long batchId = Long.valueOf(params.get("batchId").toString());
    Long receiveEnterpriseId = Long.valueOf(params.get("receiveEnterpriseId").toString());
    
    boolean success = bizConfirmationService.createConfirmedRecord(initiateEnterpriseId, batchId, receiveEnterpriseId);
    return success ? Result.success("确认记录创建成功") : Result.error("确认记录创建失败");
  }

  /**
   * 确认请求（并指定下游企业）
   */
  @PutMapping("/confirm/{confirmId}")
  public Result<String> confirmRequest(@PathVariable Long confirmId, @RequestBody(required = false) Map<String, Object> data) {
    Long downstreamEnterpriseId = null;
    if (data != null && data.containsKey("downstreamEnterpriseId")) {
      Object value = data.get("downstreamEnterpriseId");
      if (value != null) {
        downstreamEnterpriseId = Long.valueOf(value.toString());
      }
    }
    boolean success = bizConfirmationService.confirmRequest(confirmId, downstreamEnterpriseId);
    return success ? Result.success("确认成功，已自动生成您的批号") : Result.error("确认失败");
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
   * 分页查询确认请求列表（带批号信息）
   */
  @GetMapping("/page")
  public Result<Page<ConfirmationVO>> getPage(
      @RequestParam(defaultValue = "1") Integer pageNum,
      @RequestParam(defaultValue = "10") Integer pageSize,
      @RequestParam(required = false) Long receiveEnterpriseId,
      @RequestParam(required = false) Long initiateEnterpriseId,
      @RequestParam(required = false) Integer confirmStatus) {

    Page<ConfirmationVO> page = bizConfirmationService.pageConfirmationWithBatch(
        pageNum, pageSize, receiveEnterpriseId, initiateEnterpriseId, confirmStatus);
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
