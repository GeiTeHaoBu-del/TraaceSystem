package com.work.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.work.backend.entity.ProdTraceCode;
import java.util.List;

/**
 * 溯源码服务接口
 */
public interface ProdTraceCodeService extends IService<ProdTraceCode> {
  /**
   * 生成溯源码
   */
  String generateTraceCode(Long batchId);

  /**
   * 根据溯源码查询完整溯源信息
   */
  List<Object> getTraceInfo(String traceCode);
}
