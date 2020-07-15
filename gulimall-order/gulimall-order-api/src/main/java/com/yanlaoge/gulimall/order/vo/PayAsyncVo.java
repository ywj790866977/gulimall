package com.yanlaoge.gulimall.order.vo;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author rubyle
 * @date 2020/07/04
 */
@ToString
@Data
public class PayAsyncVo {

    private String gmtCreate;
    private String charset;
    private String gmtPayment;
    private LocalDateTime notifyTime;
    private String subject;
    private String sign;
    /**
     * 支付者的id
     */
    private String buyerId;
    /**
     * 订单的信息
     */
    private String body;
    /**
     * 支付金额
     */
    private BigDecimal invoiceAmount;
    private String version;
    /**
     * 通知id
     */
    private String notifyId;
    private String fundBillList;
    /**
     * 通知类型； trade_status_sync
     */
    private String notifyType;
    /**
     * 订单号
     */
    private String outTradeNo;
    /**
     * 支付的总额
     */
    private BigDecimal totalAmount;
    /**
     * 交易状态  TRADE_SUCCESS
     */
    private String tradeStatus;
    /**
     * 流水号
     */
    private String tradeNo;
    private String authAppId;
    /**
     * 商家收到的款
     */
    private BigDecimal receiptAmount;
    private BigDecimal pointAmount;
    /**
     * 应用id
     */
    private String appId;
    /**
     * 最终支付的金额
     */
    private BigDecimal buyerPayAmount;
    /**
     * 签名类型
     */
    private String signType;
    /**
     * 商家的id
     */
    private String sellerId;

}
