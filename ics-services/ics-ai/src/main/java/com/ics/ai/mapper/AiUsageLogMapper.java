package com.ics.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ics.ai.entity.AiUsageLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI 使用日志 Mapper
 */
@Mapper
public interface AiUsageLogMapper extends BaseMapper<AiUsageLog> {
}
