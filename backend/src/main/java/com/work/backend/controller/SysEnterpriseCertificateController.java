package com.work.backend.controller;

import com.work.backend.common.Result;
import com.work.backend.entity.SysEnterpriseCertificate;
import com.work.backend.service.SysEnterpriseCertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 企业证件控制器
 */
@RestController
@RequestMapping("/api/enterprise-certificate")
public class SysEnterpriseCertificateController {

  @Autowired
  private SysEnterpriseCertificateService sysEnterpriseCertificateService;

  /**
   * 获取企业所有证件
   */
  @GetMapping("/enterprise/{enterpriseId}")
  public Result<List<SysEnterpriseCertificate>> getByEnterpriseId(@PathVariable Long enterpriseId) {
    List<SysEnterpriseCertificate> list = sysEnterpriseCertificateService.getByEnterpriseId(enterpriseId);
    return Result.success(list);
  }

  /**
   * 添加企业证件
   */
  @PostMapping
  public Result<String> create(@RequestBody SysEnterpriseCertificate certificate) {
    certificate.setStatus(1);
    boolean success = sysEnterpriseCertificateService.save(certificate);
    return success ? Result.success("添加成功") : Result.error("添加失败");
  }

  /**
   * 获取证件详情
   */
  @GetMapping("/{certId}")
  public Result<SysEnterpriseCertificate> getById(@PathVariable Long certId) {
    SysEnterpriseCertificate certificate = sysEnterpriseCertificateService.getById(certId);
    return Result.success(certificate);
  }

  /**
   * 更新证件信息
   */
  @PutMapping("/{certId}")
  public Result<String> update(@PathVariable Long certId, @RequestBody SysEnterpriseCertificate certificate) {
    certificate.setCertId(certId);
    boolean success = sysEnterpriseCertificateService.updateById(certificate);
    return success ? Result.success("更新成功") : Result.error("更新失败");
  }

  /**
   * 删除证件
   */
  @DeleteMapping("/{certId}")
  public Result<String> delete(@PathVariable Long certId) {
    boolean success = sysEnterpriseCertificateService.removeById(certId);
    return success ? Result.success("删除成功") : Result.error("删除失败");
  }

  /**
   * 检查并更新过期证件
   */
  @PostMapping("/check-expired")
  public Result<String> checkExpired() {
    sysEnterpriseCertificateService.checkAndUpdateExpiredCertificates();
    return Result.success("检查完成");
  }
}
