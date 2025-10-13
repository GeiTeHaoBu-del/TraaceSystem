package com.work.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.work.backend.entity.SysUser;

/**
 * 用户服务接口
 */
public interface SysUserService extends IService<SysUser> {
  /**
   * 根据登录编码查询用户
   */
  SysUser getByLoginCode(String loginCode);

  /**
   * 用户登录
   */
  String login(String loginCode, String password);

  /**
   * 注册企业用户
   */
  boolean registerEnterpriseUser(SysUser user);
}
