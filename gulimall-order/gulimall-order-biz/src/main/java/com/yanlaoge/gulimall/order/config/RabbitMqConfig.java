package com.yanlaoge.gulimall.order.config;

import com.google.common.collect.Maps;
import com.yanlaoge.gulimall.order.constant.OrderConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Map;


/**
 * @author rubyle
 */
@Configuration
@Slf4j
public class RabbitMqConfig {
    private RabbitTemplate rabbitTemplate;
    @Resource
    private  RabbitProperties properties;

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    @Primary
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        //TODO 如果循环依赖,需要手动注入RabbitTemplate
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        this.rabbitTemplate = template;


        RabbitProperties.Template properties = this.properties.getTemplate();
        PropertyMapper map = PropertyMapper.get();
//        if (properties.getRetry().isEnabled()) {
//            template.setRetryTemplate(new RetryTemplateFactory(
//                    this.retryTemplateCustomizers.orderedStream().collect(Collectors.toList())).createRetryTemplate(
//                    properties.getRetry(), RabbitRetryTemplateCustomizer.Target.SENDER));
//        }
        map.from(properties::getReceiveTimeout).whenNonNull().as(Duration::toMillis)
                .to(template::setReceiveTimeout);
        map.from(properties::getReplyTimeout).whenNonNull().as(Duration::toMillis).to(template::setReplyTimeout);
        map.from(properties::getExchange).to(template::setExchange);
        map.from(properties::getRoutingKey).to(template::setRoutingKey);
        map.from(properties::getDefaultReceiveQueue).whenNonNull().to(template::setDefaultReceiveQueue);

        template.setMessageConverter(messageConverter());
        initRabbitTemplate();
        return template;
    }

//    @PostConstruct
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
                //防止消息发送失败
                //TODO 消息发送之后,将消息状态持久化到数据库,脚本定期扫描发送失败的消息, 进行重新发送
                log.info("[rabbitmq-confirm] CorrelationData:{}-->ack:{}-->cause:{}", correlationData, b, s);
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
                        "routingKey:{}", message, replyCode, replyText, exchange, routingKey);
            }
        });
    }


    /**
     * 死信队列
     *
     * @return queue
     */
    @Bean
    public Queue orderDelayQueue() {
        Map<String, Object> arguments = Maps.newHashMap();
        arguments.put("x-dead-letter-exchange", OrderConstant.ORDER_EVENT_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", "order.release.order");
        arguments.put("x-message-ttl", 60 * 1000 * 10);
        return new Queue(OrderConstant.ORDER_DELAY_QUEUE, true, false, false, arguments);
    }

    @Bean
    public Queue orderReleaseOrderQueue() {
        return new Queue(OrderConstant.ORDER_RELEASE_ORDER_QUEUE, true, false, false);
    }

    @Bean
    public Exchange orderEventExchange() {
        return new TopicExchange(OrderConstant.ORDER_EVENT_EXCHANGE, true, false);
    }

    @Bean
    public Binding orderCreateOrderBinding() {
        return new Binding(
                OrderConstant.ORDER_DELAY_QUEUE,
                Binding.DestinationType.QUEUE,
                OrderConstant.ORDER_EVENT_EXCHANGE,
                OrderConstant.ORDER_CREATE_ORDER,
                null
        );
    }

    @Bean
    public Binding orderReleaseOrderBinding() {
        return new Binding(
                OrderConstant.ORDER_RELEASE_ORDER_QUEUE,
                Binding.DestinationType.QUEUE,
                OrderConstant.ORDER_EVENT_EXCHANGE,
                "order.release.order",
                null
        );
    }

    /**
     * 订单释放和库存释放进行绑定
     *
     * @return binding
     */
    @Bean
    public Binding orderReleaseOtherBinding() {
        return new Binding(
                OrderConstant.STOC_RELEASE_STOCK_QUEUE,
                Binding.DestinationType.QUEUE,
                OrderConstant.ORDER_EVENT_EXCHANGE,
                "order.release.other.#",
                null
        );
    }

    @Bean
    public Queue orderSeckillOrderQueue() {
        return new Queue(OrderConstant.ORDER_SECKILL_QUEUE, true, false, false);
    }

    @Bean
    public Binding orderSeckillOrderBinding() {
        return new Binding(
                OrderConstant.ORDER_SECKILL_QUEUE,
                Binding.DestinationType.QUEUE,
                OrderConstant.ORDER_EVENT_EXCHANGE,
                OrderConstant.ORDER_SECKILL_ROUTING_KEY,
                null
        );
    }
}
