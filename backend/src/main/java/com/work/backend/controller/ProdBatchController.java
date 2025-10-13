package com.work.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.work.backend.common.Result;
import com.work.backend.entity.ProdBatch;
import com.work.backend.service.ProdBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 产品批号控制器
 */
@RestController
@RequestMapping("/api/batch")
public class ProdBatchController {

  @Autowired
  private ProdBatchService prodBatchService;

  /**
   * 创建养殖批号
   */
  @PostMapping("/breeding")
  public Result<String> createBreedingBatch(@RequestBody ProdBatch batch) {
    boolean success = prodBatchService.createBreedingBatch(batch);
    return success ? Result.success("创建成功") : Result.error("创建失败");
  }

  /**
   * 创建下游批号（屠宰/批发/零售）
   */
  @PostMapping("/downstream")
  public Result<String> createDownstreamBatch(@RequestBody ProdBatch batch) {
    boolean success = prodBatchService.createDownstreamBatch(batch);
    return success ? Result.success("创建成功") : Result.error("创建失败");
  }

  /**
   * 发布养殖批号
   */
  @PutMapping("/publish/{batchId}")
  public Result<String> publishBatch(@PathVariable Long batchId) {
    boolean success = prodBatchService.publishBreedingBatch(batchId);
    return success ? Result.success("发布成功") : Result.error("发布失败");
  }

  /**
   * 批号下架
   */
  @PutMapping("/offline/{batchId}")
  public Result<String> offlineBatch(@PathVariable Long batchId) {
    boolean success = prodBatchService.offlineBatch(batchId);
    return success ? Result.success("下架成功") : Result.error("下架失败");
  }

  /**
   * 分页查询批号列表
   */
  @GetMapping("/page")
  public Result<Page<ProdBatch>> getPage(
      @RequestParam(defaultValue = "1") Integer pageNum,
      @RequestParam(defaultValue = "10") Integer pageSize,
      @RequestParam(required = false) Long enterpriseId,
      @RequestParam(required = false) Integer batchStatus) {

    Page<ProdBatch> page = prodBatchService.pageBatch(pageNum, pageSize, enterpriseId, batchStatus);
    return Result.success(page);
  }

  /**
   * 获取批号详情
   */
  @GetMapping("/{batchId}")
  public Result<ProdBatch> getById(@PathVariable Long batchId) {
    ProdBatch batch = prodBatchService.getById(batchId);
    return Result.success(batch);
  }

  /**
   * 获取可关联的上游批号列表
   */
  @GetMapping("/available-upstream/{enterpriseType}")
  public Result<List<ProdBatch>> getAvailableUpstreamBatches(@PathVariable Integer enterpriseType) {
    List<ProdBatch> list = prodBatchService.getAvailableUpstreamBatches(enterpriseType);
    return Result.success(list);
  }

  /**
   * 溯源查询
   */
  @GetMapping("/trace/{batchId}")
  public Result<List<ProdBatch>> traceBack(@PathVariable Long batchId) {
    List<ProdBatch> traceList = prodBatchService.traceBack(batchId);
    return Result.success(traceList);
  }

  /**
   * 更新批号信息
   */
  @PutMapping("/{batchId}")
  public Result<String> update(@PathVariable Long batchId, @RequestBody ProdBatch batch) {
    batch.setBatchId(batchId);
    boolean success = prodBatchService.updateById(batch);
    return success ? Result.success("更新成功") : Result.error("更新失败");
  }

  /**
   * 删除批号
   */
  @DeleteMapping("/{batchId}")
  public Result<String> delete(@PathVariable Long batchId) {
    boolean success = prodBatchService.removeById(batchId);
    return success ? Result.success("删除成功") : Result.error("删除失败");
  }
}
