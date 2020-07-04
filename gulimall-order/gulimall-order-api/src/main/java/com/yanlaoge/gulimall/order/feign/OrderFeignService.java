package com.yanlaoge.gulimall.order.feign;

import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.common.utils.ResponseVo;
import com.yanlaoge.gulimall.order.entity.OrderEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    /**
     * 查询用户订单信息
     *
     * @param params 参数
     * @return R
     */
    @PostMapping("order/order/listWithItem")
    ResponseVo<PageUtils> listWithItem(@RequestBody Map<String, Object> params);
}
