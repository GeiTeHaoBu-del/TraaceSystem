package com.work.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 溯源码表实体类
 */
@Data
@TableName("prod_trace_code")
public class ProdTraceCode {

  @TableId(value = "trace_id", type = IdType.AUTO)
  private Long traceId;

  private String traceCode;

  private Long batchId;

  private LocalDateTime generateTime;

  private Integer status; // 0-无效，1-有效

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @TableField(exist = false)
  private String batchNo;

  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;
}
