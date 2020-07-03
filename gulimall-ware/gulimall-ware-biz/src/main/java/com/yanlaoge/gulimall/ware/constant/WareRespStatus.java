package com.yanlaoge.gulimall.ware.constant;

import com.yanlaoge.common.utils.ServiceStatusCode;

/**
 * @author 好人
 * @date 2020-07-01 13:20
 **/
public class WareRespStatus {
    public static final ServiceStatusCode REMOTE_ERROR = new ServiceStatusCode(21000,"远程调用服务失败");
    public static final ServiceStatusCode STOCK_ERROR = new ServiceStatusCode(21001,"商品无库存");
    public static final ServiceStatusCode STOCK_LOCK_ERROR = new ServiceStatusCode(21002,"锁定库存失败");
    public static final ServiceStatusCode STOCK_LOCK_STATUS_ERROR = new ServiceStatusCode(21003,"任务单状态已变更");
}
