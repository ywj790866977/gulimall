package com.yanlaoge.gulimall.ware.config;

import com.google.common.collect.Maps;
import com.yanlaoge.gulimall.ware.constant.WareConsTant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author rubyle
 * @date 2020/07/03
 */
@Configuration
public class RabbitMqConfig {
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Exchange stockEventExchange() {
        return new TopicExchange(WareConsTant.STOCK_EVENT_EXCHANGE, true, false);
    }

    @Bean
    public Queue stockReleaseStockQueue() {
        return new Queue(WareConsTant.STOCK_RELEASE_STOCK_QUEUE, true, false, false);
    }

    @Bean
    public Queue stockDelayQueue() {
        Map<String, Object> arguments = Maps.newHashMap();
        arguments.put("x-dead-letter-exchange", WareConsTant.STOCK_EVENT_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", "stock.release");
        arguments.put("x-message-ttl", 60*1000*15);
        return new Queue(WareConsTant.STOCK_DELAY_QUEUE, true, false, false, arguments);
    }

    @Bean
    public Binding stockLockBinding() {
        return new Binding(
                WareConsTant.STOCK_RELEASE_STOCK_QUEUE, Binding.DestinationType.QUEUE,
                WareConsTant.STOCK_EVENT_EXCHANGE,
                "stock.release.#",
                null
        );
    }

    @Bean
    public Binding stockReleaseBinding() {
        return new Binding(
                WareConsTant.STOCK_DELAY_QUEUE, Binding.DestinationType.QUEUE,
                WareConsTant.STOCK_EVENT_EXCHANGE,
                WareConsTant.STOCK_LOCKED,
                null
        );
    }
}
