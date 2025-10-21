package com.work.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.work.backend.entity.ProdBatch;
import java.util.List;

/**
 * 产品批号服务接口
 */
public interface ProdBatchService extends IService<ProdBatch> {
  /**
   * 创建养殖批号
   */
  boolean createBreedingBatch(ProdBatch batch);

  /**
   * 创建下游批号（屠宰/批发/零售）
   */
  boolean createDownstreamBatch(ProdBatch batch);

  /**
   * 批号下架
   */
  boolean offlineBatch(Long batchId);

  /**
   * 分页查询批号
   */
  Page<ProdBatch> pageBatch(int pageNum, int pageSize, Long enterpriseId, Integer batchStatus);

  /**
   * 获取可关联的上游批号列表（指定了当前企业的批号）
   */
  List<ProdBatch> getAvailableUpstreamBatches(Long currentEnterpriseId);

  /**
   * 溯源查询
   */
  List<ProdBatch> traceBack(Long batchId);
}
