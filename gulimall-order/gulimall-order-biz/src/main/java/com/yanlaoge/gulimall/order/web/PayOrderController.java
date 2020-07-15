package com.yanlaoge.gulimall.order.web;

import com.yanlaoge.gulimall.order.common.AlipayTemplate;
import com.yanlaoge.gulimall.order.service.OrderService;
import com.yanlaoge.gulimall.order.vo.PayVo;
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
    private AlipayTemplate alipayTemplate;
    @Resource
    private OrderService orderService;

    @ResponseBody
    @GetMapping(value = "/payOrder", produces = "text/html")
    public String payOrder(@RequestParam("orderSn") String orderSn) {
        PayVo payVo = orderService.getOrderPay(orderSn);
        return  alipayTemplate.pay(payVo);
    }

}
