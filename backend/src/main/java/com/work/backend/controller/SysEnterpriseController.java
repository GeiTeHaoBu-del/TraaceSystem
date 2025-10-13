package com.work.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.work.backend.common.Result;
import com.work.backend.entity.SysEnterprise;
import com.work.backend.service.SysEnterpriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 企业控制器
 */
@RestController
@RequestMapping("/api/enterprise")
public class SysEnterpriseController {

  @Autowired
  private SysEnterpriseService sysEnterpriseService;

  /**
   * 分页查询企业列表
   */
  @GetMapping("/page")
  public Result<Page<SysEnterprise>> getPage(
      @RequestParam(defaultValue = "1") Integer pageNum,
      @RequestParam(defaultValue = "10") Integer pageSize,
      @RequestParam(required = false) Integer enterpriseType,
      @RequestParam(required = false) String province) {

    Page<SysEnterprise> page = sysEnterpriseService.pageEnterprise(pageNum, pageSize, enterpriseType, province);
    return Result.success(page);
  }

  /**
   * 创建企业
   */
  @PostMapping
  public Result<String> create(@RequestBody SysEnterprise enterprise) {
    enterprise.setStatus(1);
    boolean success = sysEnterpriseService.save(enterprise);
    return success ? Result.success("创建成功") : Result.error("创建失败");
  }

  /**
   * 获取企业详情
   */
  @GetMapping("/{enterpriseId}")
  public Result<SysEnterprise> getById(@PathVariable Long enterpriseId) {
    SysEnterprise enterprise = sysEnterpriseService.getById(enterpriseId);
    return Result.success(enterprise);
  }

  /**
   * 更新企业信息
   */
  @PutMapping("/{enterpriseId}")
  public Result<String> update(@PathVariable Long enterpriseId, @RequestBody SysEnterprise enterprise) {
    enterprise.setEnterpriseId(enterpriseId);
    boolean success = sysEnterpriseService.updateById(enterprise);
    return success ? Result.success("更新成功") : Result.error("更新失败");
  }

  /**
   * 删除企业
   */
  @DeleteMapping("/{enterpriseId}")
  public Result<String> delete(@PathVariable Long enterpriseId) {
    boolean success = sysEnterpriseService.removeById(enterpriseId);
    return success ? Result.success("删除成功") : Result.error("删除失败");
  }

  /**
   * 启用/禁用企业
   */
  @PutMapping("/status/{enterpriseId}")
  public Result<String> updateStatus(@PathVariable Long enterpriseId, @RequestBody Map<String, Integer> data) {
    Integer status = data.get("status");
    SysEnterprise enterprise = new SysEnterprise();
    enterprise.setEnterpriseId(enterpriseId);
    enterprise.setStatus(status);
    boolean success = sysEnterpriseService.updateById(enterprise);
    return success ? Result.success("状态更新成功") : Result.error("状态更新失败");
  }

  /**
   * 获取企业注册统计数据
   */
  @GetMapping("/statistics")
  public Result<Map<String, Object>> getStatistics() {
    Map<String, Object> statistics = sysEnterpriseService.getRegisterStatistics();
    return Result.success(statistics);
  }

  /**
   * 获取所有企业列表（不分页）
   */
  @GetMapping("/list")
  public Result<java.util.List<SysEnterprise>> getList(
      @RequestParam(required = false) Integer enterpriseType) {

    com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysEnterprise> wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();

    if (enterpriseType != null) {
      wrapper.eq(SysEnterprise::getEnterpriseType, enterpriseType);
    }
    wrapper.eq(SysEnterprise::getStatus, 1);

    return Result.success(sysEnterpriseService.list(wrapper));
  }
}
