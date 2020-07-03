package com.yanlaoge.gulimall.ware.listener;

import com.rabbitmq.client.Channel;
import com.yanlaoge.gulimall.ware.constant.WareConsTant;
import com.yanlaoge.gulimall.ware.dto.StockLockedDto;
import com.yanlaoge.gulimall.ware.service.WareSkuService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author 好人
 * @date 2020-07-03 10:59
 **/
@RabbitListener(queues = WareConsTant.STOC_RELEASE_STOCK_QUEUE)
@Component
@Slf4j
public class ReleaseLockStockListener {

    @Resource
    private WareSkuService wareSkuService;

    /**
     * 库存自动解锁
     *
     * @param dto     消息实体
     * @param message 消息
     */
    @SneakyThrows
    @RabbitHandler
    public void handleStockLockedRelease(StockLockedDto dto, Message message, Channel channel) {
        try {
            log.info("[handleStockLockedRelease] accept message = {} , dto = {}", message, dto);
            wareSkuService.releaseStockLock(dto);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            log.info("[handleStockLockedRelease] is ok , dto = {}",dto);
        } catch (Exception e) {
            log.error("[handleStockLockedRelease] is error ",e);
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }

}
