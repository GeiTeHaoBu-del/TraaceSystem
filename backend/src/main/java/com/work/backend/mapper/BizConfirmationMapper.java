package com.work.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.work.backend.entity.BizConfirmation;
import com.work.backend.vo.ConfirmationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 确认请求 Mapper
 */
@Mapper
public interface BizConfirmationMapper extends BaseMapper<BizConfirmation> {
    
    /**
     * 分页查询确认请求详情(关联批号和企业信息)
     */
    @Select("<script>" +
            "SELECT " +
            "  c.confirm_id AS confirmId, " +
            "  c.initiate_enterprise_id AS initiateEnterpriseId, " +
            "  c.receive_enterprise_id AS receiveEnterpriseId, " +
            "  c.batch_id AS batchId, " +
            "  c.confirm_status AS confirmStatus, " +
            "  c.initiate_time AS initiateTime, " +
            "  c.confirm_time AS confirmTime, " +
            "  c.reject_reason AS rejectReason, " +
            "  e1.enterprise_name AS initiateEnterpriseName, " +
            "  e2.enterprise_name AS receiveEnterpriseName, " +
            "  b.batch_no AS batchNo, " +
            "  b.product_variety AS productVariety, " +
            "  b.cert_no AS certNo, " +
            "  b.batch_status AS batchStatus, " +
            "  b.upstream_batch_id AS upstreamBatchId, " +
            "  b.downstream_enterprise_id AS downstreamEnterpriseId " +
            "FROM biz_confirmation c " +
            "LEFT JOIN sys_enterprise e1 ON c.initiate_enterprise_id = e1.enterprise_id " +
            "LEFT JOIN sys_enterprise e2 ON c.receive_enterprise_id = e2.enterprise_id " +
            "LEFT JOIN prod_batch b ON c.batch_id = b.batch_id " +
            "WHERE 1=1 " +
            "<if test='receiveEnterpriseId != null'> AND c.receive_enterprise_id = #{receiveEnterpriseId} </if>" +
            "<if test='initiateEnterpriseId != null'> AND c.initiate_enterprise_id = #{initiateEnterpriseId} </if>" +
            "<if test='confirmStatus != null'> AND c.confirm_status = #{confirmStatus} </if>" +
            "ORDER BY c.initiate_time DESC" +
            "</script>")
    Page<ConfirmationVO> selectConfirmationPage(Page<ConfirmationVO> page, 
                                                 @Param("receiveEnterpriseId") Long receiveEnterpriseId,
                                                 @Param("initiateEnterpriseId") Long initiateEnterpriseId,
                                                 @Param("confirmStatus") Integer confirmStatus);
}
