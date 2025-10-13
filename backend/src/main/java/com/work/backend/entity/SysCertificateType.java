package com.work.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 证件类型表实体类
 */
@Data
@TableName("sys_certificate_type")
public class SysCertificateType {

  @TableId(value = "cert_type_id", type = IdType.AUTO)
  private Integer certTypeId;

  private String certTypeName;

  private String description;

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;
}
