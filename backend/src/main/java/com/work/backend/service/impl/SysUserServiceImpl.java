package com.work.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.work.backend.entity.SysUser;
import com.work.backend.mapper.SysUserMapper;
import com.work.backend.service.SysUserService;
import cn.hutool.crypto.digest.BCrypt;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

  @Override
  public SysUser getByLoginCode(String loginCode) {
    return this.lambdaQuery()
        .eq(SysUser::getLoginCode, loginCode)
        .one();
  }

  @Override
  public String login(String loginCode, String password) {
    SysUser user = getByLoginCode(loginCode);
    if (user == null) {
      throw new RuntimeException("用户不存在");
    }
    if (user.getStatus() == 0) {
      throw new RuntimeException("用户已被禁用");
    }
    if (!BCrypt.checkpw(password, user.getPassword())) {
      throw new RuntimeException("密码错误");
    }
    // TODO: 生成JWT Token
    return "token_" + user.getUserId();
  }

  @Override
  public boolean registerEnterpriseUser(SysUser user) {
    // 检查登录编码是否已存在
    SysUser existUser = getByLoginCode(user.getLoginCode());
    if (existUser != null) {
      throw new RuntimeException("登录编码已存在");
    }
    // 密码加密
    user.setPassword(BCrypt.hashpw(user.getPassword()));
    user.setStatus(1);
    return this.save(user);
  }
}
