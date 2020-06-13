package com.yanlaoge.gulimall.order.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author rubyle
 */
@Configuration
@Slf4j
public class RabbitMqConfig {
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @PostConstruct
    public void initRabbitTemplate() {
        //设置确认回调(服务器收到消息就回调)
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             *
             * @param correlationData 当前消息管理关联数据 (唯一id)
             * @param b 是否成功收到
             * @param s 失败原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {
                log.info("[rabbitmq-confirm] CorrelationData:{}-->ack:{}-->cause:{}",correlationData,b,s);
            }
        });

        //设置消息抵达回调
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             * 消息眉头投递给指定队列, 触发
             * @param message 消息
             * @param replyCode 状态码
             * @param replyText 文本内容
             * @param exchange 交换机
             * @param routingKey 路邮键
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                log.info("[rabbitmq-returnedMessage] message:{}-->replyCode:{}-->replyText:{}-->echange:{}-->" +
                                "routingKey:{}",message,replyCode, replyText,exchange,routingKey);
            }
        });
    }
}
