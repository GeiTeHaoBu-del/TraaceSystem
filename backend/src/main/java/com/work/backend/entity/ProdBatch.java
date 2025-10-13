package com.work.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 产品批号表实体类
 */
@Data
@TableName("prod_batch")
public class ProdBatch {

  @TableId(value = "batch_id", type = IdType.AUTO)
  private Long batchId;

  private String batchNo;

  private Long enterpriseId;

  private Long upstreamBatchId;

  private String productVariety;

  private String certNo;

  private Integer batchStatus; // 养殖：0-待发布，1-已发布，2-已下架；屠宰/批发/零售：0-新建，1-待确认，2-已确认，3-已下架

  private LocalDateTime offlineTime;

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;
}
