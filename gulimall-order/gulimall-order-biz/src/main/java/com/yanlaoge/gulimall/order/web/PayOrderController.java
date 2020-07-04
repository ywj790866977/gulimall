package com.yanlaoge.gulimall.order.web;

import com.yanlaoge.common.utils.ResponseVo;
import com.yanlaoge.gulimall.order.service.OrderService;
import com.yanlaoge.gulimall.thirdparty.feign.ThridPartyFeignService;
import com.yanlaoge.gulimall.thirdparty.vo.PayVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author 好人
 * @date 2020-07-04 15:09
 **/
@Controller
public class PayOrderController {

    @Resource
    private ThridPartyFeignService thridPartyFeignService;
    @Resource
    private OrderService orderService;

    @ResponseBody
    @GetMapping(value = "/payOrder", produces = "text/html")
    public String payOrder(@RequestParam("orderSn") String orderSn) {
        PayVo payVo = orderService.getOrderPay(orderSn);
        ResponseVo<String> responseVo = thridPartyFeignService.aliPay(payVo);
        if (responseVo == null || responseVo.getCode() != 0) {
            return "pay";
        }
        return responseVo.getData();
    }

}
