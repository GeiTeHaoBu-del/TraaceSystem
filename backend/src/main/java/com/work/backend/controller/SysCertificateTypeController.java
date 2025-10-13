package com.work.backend.controller;

import com.work.backend.common.Result;
import com.work.backend.entity.SysCertificateType;
import com.work.backend.service.SysCertificateTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 证件类型控制器
 */
@RestController
@RequestMapping("/api/certificate-type")
public class SysCertificateTypeController {

  @Autowired
  private SysCertificateTypeService sysCertificateTypeService;

  /**
   * 获取所有证件类型
   */
  @GetMapping("/list")
  public Result<List<SysCertificateType>> getList() {
    List<SysCertificateType> list = sysCertificateTypeService.list();
    return Result.success(list);
  }

  /**
   * 根据企业类型获取适用的证件类型
   */
  @GetMapping("/by-enterprise-type/{enterpriseType}")
  public Result<List<SysCertificateType>> getByEnterpriseType(@PathVariable Integer enterpriseType) {
    List<SysCertificateType> list = sysCertificateTypeService.getByEnterpriseType(enterpriseType);
    return Result.success(list);
  }

  /**
   * 获取证件类型详情
   */
  @GetMapping("/{certTypeId}")
  public Result<SysCertificateType> getById(@PathVariable Integer certTypeId) {
    SysCertificateType certType = sysCertificateTypeService.getById(certTypeId);
    return Result.success(certType);
  }

  /**
   * 创建证件类型
   */
  @PostMapping
  public Result<String> create(@RequestBody SysCertificateType certType) {
    boolean success = sysCertificateTypeService.save(certType);
    return success ? Result.success("创建成功") : Result.error("创建失败");
  }

  /**
   * 更新证件类型
   */
  @PutMapping("/{certTypeId}")
  public Result<String> update(@PathVariable Integer certTypeId, @RequestBody SysCertificateType certType) {
    certType.setCertTypeId(certTypeId);
    boolean success = sysCertificateTypeService.updateById(certType);
    return success ? Result.success("更新成功") : Result.error("更新失败");
  }

  /**
   * 删除证件类型
   */
  @DeleteMapping("/{certTypeId}")
  public Result<String> delete(@PathVariable Integer certTypeId) {
    boolean success = sysCertificateTypeService.removeById(certTypeId);
    return success ? Result.success("删除成功") : Result.error("删除失败");
  }
}
