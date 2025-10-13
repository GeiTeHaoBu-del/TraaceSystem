package com.work.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.work.backend.entity.BizConfirmation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 确认请求 Mapper
 */
@Mapper
public interface BizConfirmationMapper extends BaseMapper<BizConfirmation> {
}
