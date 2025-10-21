package com.work.backend.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 确认请求详情VO
 */
@Data
public class ConfirmationVO {
    // 确认请求基本信息
    private Long confirmId;
    private Long initiateEnterpriseId;
    private String initiateEnterpriseName;
    private Long receiveEnterpriseId;
    private String receiveEnterpriseName;
    private Long batchId;
    private Integer confirmStatus;
    private LocalDateTime initiateTime;
    private LocalDateTime confirmTime;
    private String rejectReason;
    
    // 批号相关信息
    private String batchNo;
    private String productVariety;
    private String certNo;
    private Integer batchStatus;
    private Long upstreamBatchId;
    private Long downstreamEnterpriseId;
}
