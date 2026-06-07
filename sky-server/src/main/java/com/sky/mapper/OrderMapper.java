package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    /**
     * 提交订单
     * @param orders
     * @return
     */
    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 历史订单查询
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> page(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 查看订单详细
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    /**
     * 取消订单
     * @param id
     * @return
     */
    @Delete("delete from orders where id = #{id}")
    void delete(Long id);

    /**
     *
     * @return
     */
    @Select("select count(*) from orders")
    Integer getAllCount();

    /**
     * 各个状态的订单数量统计
     * @return
     */
    @Select("select count(*) from orders where status = #{status}")
    Integer getCountByStstus(Integer status);

    /**
     * 根据状态查找订单
     * @param status
     * @return
     */
    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrderTime(Integer status, LocalDateTime orderTime);

    /**
     * 获得一段时间内的订单数
     * @param status
     * @param beginTime
     * @param endTime
     * @return
     */
    Integer getCountByStatusAndOrderTime(Integer status, LocalDateTime beginTime, LocalDateTime endTime);


}
