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
 * 企业资质证件表实体类
 */
@Data
@TableName("sys_enterprise_certificate")
public class SysEnterpriseCertificate {

  @TableId(value = "cert_id", type = IdType.AUTO)
  private Long certId;

  private Long enterpriseId;

  private Integer certTypeId;

  private String certNo;

  private LocalDate validUntil;

  private Integer status; // 0-过期/无效，1-有效

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;
}
