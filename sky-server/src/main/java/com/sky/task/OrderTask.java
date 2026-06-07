package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    OrderMapper orderMapper;

    /**
     * 取消未支付的超时订单
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void orderTimeOutTask() {
        log.info("取消未支付的超时订单");

        LocalDateTime orderTime = LocalDateTime.now().minusMinutes(15);
        List<Orders> list = orderMapper.getByStatusAndOrderTime(Orders.UN_PAID,orderTime);

        if (!list.isEmpty()) {
            for (Orders orders : list) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时，自动取消");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }

    }

    /**
     * 完成长期派送中的订单
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void processDeliverOrder(){
        log.info("完成长期派送中的订单");
        LocalDateTime orderTime = LocalDateTime.now().minusHours(2);

        List<Orders> list = orderMapper.getByStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS,orderTime);

        if (!list.isEmpty()) {
            for (Orders orders : list) {
                orders.setStatus(Orders.COMPLETED);
                orders.setDeliveryTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }
    }
}
