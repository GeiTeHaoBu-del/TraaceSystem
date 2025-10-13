package com.work.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 确认请求表实体类
 */
@Data
@TableName("biz_confirmation")
public class BizConfirmation {

  @TableId(value = "confirm_id", type = IdType.AUTO)
  private Long confirmId;

  private Long initiateEnterpriseId;

  private Long receiveEnterpriseId;

  private Long batchId;

  private Integer confirmStatus; // 0-待确认，1-已确认，2-已拒绝

  private LocalDateTime initiateTime;

  private LocalDateTime confirmTime;

  private String rejectReason;

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;
}
