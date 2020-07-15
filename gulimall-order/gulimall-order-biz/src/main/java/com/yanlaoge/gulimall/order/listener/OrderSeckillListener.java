package com.yanlaoge.gulimall.order.listener;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.yanlaoge.gulimall.order.constant.OrderConstant;
import com.yanlaoge.gulimall.order.service.OrderService;
import com.yanlaoge.gulimall.seckill.to.SeckillOrderTo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author 好人
 * @date 2020-07-11 18:17
 **/
@Slf4j
@Component
@RabbitListener(queues = OrderConstant.ORDER_SECKILL_QUEUE)
public class OrderSeckillListener {
    @Resource
    private OrderService orderService;

    @SneakyThrows
    @RabbitHandler
    public void handlerOrderSeckill(SeckillOrderTo seckillOrderTo, Channel channel, Message message){
        try {
            log.info("[handlerOrderSeckill] seckill , SeckillOrderTo = {}",seckillOrderTo);
            orderService.createSeckillOrder(seckillOrderTo);
            //TODO 支付宝手动收单
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            log.info("[handlerOrderSeckill] is ok");
        } catch (Exception e) {
            log.error("[handlerOrderSeckill] is error ",e);
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }
}
