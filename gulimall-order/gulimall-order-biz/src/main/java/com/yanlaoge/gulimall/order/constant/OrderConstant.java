package com.yanlaoge.gulimall.order.constant;

/**
 * @author 好人
 * @date 2020-06-30 18:31
 **/
public class OrderConstant {
    public static final String USER_ORDER_TOKEN_PRE = "order:token:";
    public static final String ORDER_EVENT_EXCHANGE = "order-event-exchange";
    public static final String ORDER_DELAY_QUEUE = "order.delay.queue";
    public static final String ORDER_RELEASE_ORDER_QUEUE = "order.release.order.queue";
    public static final String ORDER_CREATE_ORDER = "order.create.order";
    public static final String STOC_RELEASE_STOCK_QUEUE = "stock.release.stock.queue";
    public static final String ORDER_SECKILL_ROUTING_KEY = "order.seckill.order";
    public static final String ORDER_SECKILL_QUEUE= "order.seckill.order.queue";

    public static final double PRICE_DIFFERENCE = 0.01;
}
