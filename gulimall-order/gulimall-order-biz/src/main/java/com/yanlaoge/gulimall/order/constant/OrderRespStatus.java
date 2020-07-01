package com.yanlaoge.gulimall.order.constant;

import com.yanlaoge.common.utils.ServiceStatusCode;

/**
 * @author 好人
 * @date 2020-07-01 13:20
 **/
public class OrderRespStatus {
    public static final ServiceStatusCode TOKEND_ERROR = new ServiceStatusCode(12001,"令牌校验失败");
    public static final ServiceStatusCode PRICE_ERROR = new ServiceStatusCode(12002,"价格校验失败");
    public static final ServiceStatusCode STOCK_ERROR = new ServiceStatusCode(12003,"库存锁定失败");
}
