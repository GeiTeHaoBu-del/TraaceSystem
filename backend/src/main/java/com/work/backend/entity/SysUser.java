package com.work.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户表实体类
 */
@Data
@TableName("sys_user")
public class SysUser {

  @TableId(value = "user_id", type = IdType.AUTO)
  private Long userId;

  private Integer userType; // 0-系统管理员，1-养殖企业，2-屠宰企业，3-批发企业，4-零售企业

  private String loginCode;

  private String password;

  private Long enterpriseId;

  private Integer status; // 0-禁用，1-正常

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;
}
