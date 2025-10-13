package com.work.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 证件类型-适用企业关联表实体类
 */
@Data
@TableName("sys_cert_type_apply_enterprise")
public class SysCertTypeApplyEnterprise {

  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  private Integer certTypeId;

  private Integer applyEnterpriseType; // 1-养殖，2-屠宰，3-批发，4-零售

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;
}
