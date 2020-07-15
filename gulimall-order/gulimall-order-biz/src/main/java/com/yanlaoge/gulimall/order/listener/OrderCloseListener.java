package com.yanlaoge.gulimall.order.listener;

import com.rabbitmq.client.Channel;
import com.yanlaoge.gulimall.order.constant.OrderConstant;
import com.yanlaoge.gulimall.order.entity.OrderEntity;
import com.yanlaoge.gulimall.order.service.OrderService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author 好人
 * @date 2020-07-03 14:55
 **/
@RabbitListener(queues = OrderConstant.ORDER_RELEASE_ORDER_QUEUE)
@Service
@Slf4j
public class OrderCloseListener {
    @Resource
    private OrderService orderService;

    @SneakyThrows
    @RabbitHandler
    public void handlerOrderClose(OrderEntity orderEntity, Message message, Channel channel){
        try {
            log.info("[handlerOrderClose] orderclose , orderEntity = {}",orderEntity);
            orderService.closeOrder(orderEntity);
            //TODO 支付宝手动收单
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            log.info("[handlerOrderClose] is ok");
        } catch (Exception e) {
            log.error("[handlerOrderClose] is error ",e);
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }

    }
}
