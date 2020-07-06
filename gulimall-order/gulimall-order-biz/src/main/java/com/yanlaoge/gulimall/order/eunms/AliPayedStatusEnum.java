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
    WAIT_BUYER_PAY(1, "WAIT_BUYER_PAY"),
    /**
     * 未付款交易超时关闭，或支付完成后全额退款
     */
    TRADE_CLOSED(2, "TRADE_CLOSED"),
    /**
     * 交易支付成功
     */
    TRADE_SUCCESS(3, "TRADE_SUCCESS"),
    /**
     * 交易结束，不可退款
     */
    TRADE_FINISHED(4, "TRADE_FINISHED");
    private Integer code;
    private String msg;

    public static Integer getValueByStatus(String status) {
        for (AliPayedStatusEnum platformFree : AliPayedStatusEnum.values()) {
            if (platformFree.getMsg().equals(status)) {
                return platformFree.getCode();
            }
        }
        return 0;
    }
}
