package com.yanlaoge.gulimall.order.feign;

import com.yanlaoge.common.utils.ResponseVo;
import com.yanlaoge.gulimall.order.entity.OrderEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author 好人
 * @date 2020-07-03 11:20
 **/
@FeignClient("gulimall-order")
public interface OrderFeignService {

    /**
     * 查询订单
     *
     * @param orderSn 订单号
     * @return R
     */
    @GetMapping("order/order/status/{orderSn}")
    ResponseVo<OrderEntity> getOrder(@PathVariable("orderSn") String orderSn);
}
