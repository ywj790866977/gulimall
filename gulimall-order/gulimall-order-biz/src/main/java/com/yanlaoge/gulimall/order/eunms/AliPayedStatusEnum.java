package com.yanlaoge.gulimall.order.eunms;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author rubyle
 * @date 2020/07/05
 */
@Getter
@AllArgsConstructor
public enum AliPayedStatusEnum {
    /**
     * 交易创建，等待买家付款
     */
    WAIT_BUYER_PAY(1,"WAIT_BUYER_PAY","交易创建，等待买家付款"),
    /**
     * 未付款交易超时关闭，或支付完成后全额退款
     */
    TRADE_CLOSED(2,"TRADE_CLOSED","未付款交易超时关闭，或支付完成后全额退款"),
    /**
     * 交易支付成功
     */
    TRADE_SUCCESS(3,"TRADE_SUCCESS","交易支付成功"),
    /**
     * 交易结束，不可退款
     */
    TRADE_FINISHED(4,"TRADE_FINISHED","交易结束，不可退款");
    private Integer code;
    private String msg;
    private String remak;
}
