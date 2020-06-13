package com.yanlaoge.gulimall.order;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestDemo01 {


    @Resource
    private AmqpAdmin amqpAdmin;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Test
    public void createExchange(){
        DirectExchange directExchange = new DirectExchange("test-exchange", true, false);
        amqpAdmin.declareExchange(directExchange);
    }

    @Test
    public void createQueue(){
        Queue queue = new Queue("test-queue",true,false,false);
        amqpAdmin.declareQueue(queue);
    }

    @Test
    public void createBinding(){
        Binding binding = new Binding("test-queue", Binding.DestinationType.QUEUE,"test-exchange","test.java",null);
        amqpAdmin.declareBinding(binding);
    }


    @Test
    public void sendMsg(){
        rabbitTemplate.convertAndSend("test-exchange","test.java","hello world!");
    }
}
