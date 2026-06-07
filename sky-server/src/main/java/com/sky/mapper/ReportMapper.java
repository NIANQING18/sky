package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ReportMapper {

    /**
     * 营业额统计
     * @param beginTime
     * @param endTime
     * @param status
     * @return
     */
    @Select("select sum(amount) from orders where order_time between #{beginTime} and #{endTime} and status = #{status}")
    Double turnoverStatistics(LocalDateTime beginTime, LocalDateTime endTime, Integer status);

    @Select("select count(*) from user where create_time < #{endTime}")
    Integer getTotalUser(LocalDateTime endTime);

    @Select("select count(*) from user where create_time between #{beginTime} and #{endTime}")
    Integer getNewUser(LocalDateTime beginTime, LocalDateTime endTime);

    List<GoodsSalesDTO> selectTop10(LocalDateTime beginTime, LocalDateTime endTime);
}
