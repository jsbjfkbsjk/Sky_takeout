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
    private OrderMapper orderMapper;


    @Scheduled(cron="0 * * * * ?")
    public void processTimeOut(){
        log.info("定时处理超时订单{}", LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        List<Orders>orders = orderMapper.getBYStatusAndTime(Orders.PENDING_PAYMENT,time);
        if(orders!=null&& !orders.isEmpty()){
            for(Orders order:orders){
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason("订单超时自动取消");
                order.setCancelTime(LocalDateTime.now());
                orderMapper.update(order);
            }
        }


    }

    @Scheduled(cron="0 0 1 * * ?")
    public  void processDeliveryOrder(){
        log.info("定时处理派送中的订单{}", LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().plusHours(-1);
        List<Orders> orders = orderMapper.getBYStatusAndTime(Orders.DELIVERY_IN_PROGRESS, time);
        if(orders!=null&& !orders.isEmpty()){
            for(Orders order:orders){
                order.setStatus(Orders.COMPLETED);
                order.setCancelReason("订单次日自动完成派送");
                order.setCancelTime(LocalDateTime.now());
                orderMapper.update(order);
            }
        }


    }
}
