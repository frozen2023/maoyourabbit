package com.chen.mapper;

import com.chen.pojo.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Frozen
 * @since 2023-03-22
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

}
