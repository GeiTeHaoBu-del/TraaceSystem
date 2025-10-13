package com.work.backend.controller;

import com.work.backend.common.Result;
import com.work.backend.entity.SysUser;
import com.work.backend.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
public class SysUserController {

  @Autowired
  private SysUserService sysUserService;

  /**
   * 用户登录
   */
  @PostMapping("/login")
  public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
    String loginCode = loginData.get("loginCode");
    String password = loginData.get("password");

    String token = sysUserService.login(loginCode, password);
    SysUser user = sysUserService.getByLoginCode(loginCode);

    Map<String, Object> result = new HashMap<>();
    result.put("token", token);
    result.put("userInfo", user);

    return Result.success(result);
  }

  /**
   * 注册企业用户
   */
  @PostMapping("/register")
  public Result<String> register(@RequestBody SysUser user) {
    boolean success = sysUserService.registerEnterpriseUser(user);
    return success ? Result.success("注册成功") : Result.error("注册失败");
  }

  /**
   * 获取用户信息
   */
  @GetMapping("/{userId}")
  public Result<SysUser> getUserInfo(@PathVariable Long userId) {
    SysUser user = sysUserService.getById(userId);
    return Result.success(user);
  }

  /**
   * 更新用户信息
   */
  @PutMapping("/{userId}")
  public Result<String> updateUser(@PathVariable Long userId, @RequestBody SysUser user) {
    user.setUserId(userId);
    boolean success = sysUserService.updateById(user);
    return success ? Result.success("更新成功") : Result.error("更新失败");
  }

  /**
   * 修改密码
   */
  @PutMapping("/password/{userId}")
  public Result<String> updatePassword(@PathVariable Long userId, @RequestBody Map<String, String> data) {
    String oldPassword = data.get("oldPassword");
    String newPassword = data.get("newPassword");

    SysUser user = sysUserService.getById(userId);
    if (user == null) {
      return Result.error("用户不存在");
    }

    // 验证旧密码
    if (!cn.hutool.crypto.digest.BCrypt.checkpw(oldPassword, user.getPassword())) {
      return Result.error("原密码错误");
    }

    // 更新密码
    user.setPassword(cn.hutool.crypto.digest.BCrypt.hashpw(newPassword));
    boolean success = sysUserService.updateById(user);

    return success ? Result.success("密码修改成功") : Result.error("密码修改失败");
  }
}
