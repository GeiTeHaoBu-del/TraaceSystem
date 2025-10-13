package com.work.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 企业表实体类
 */
@Data
@TableName("sys_enterprise")
public class SysEnterprise {

  @TableId(value = "enterprise_id", type = IdType.AUTO)
  private Long enterpriseId;

  private String enterpriseName;

  private Integer enterpriseType; // 1-养殖，2-屠宰，3-批发，4-零售

  private String province;

  private LocalDate registerTime;

  private String contactPhone;

  private String address;

  private Integer status; // 0-禁用，1-正常

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;
}
