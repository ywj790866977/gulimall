package com.yanlaoge.gulimall.order.listener;

import com.yanlaoge.gulimall.order.service.OrderService;
import com.yanlaoge.gulimall.thirdparty.vo.PayAsyncVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author rubule
 * @date 2020/07/05
 */
@RestController
@Slf4j
public class OrderPayedListener {
    @Resource
    private OrderService orderService;

    @PostMapping("/payed/notify")
    public String handlerAlipayed(@RequestBody PayAsyncVo vo, HttpServletRequest request){
        //收到支付宝的通之后,需要回应
        log.info("[handlerAlipayed] 入参 :{}",vo);
        //1.验签

        //2. 修改
        String res = orderService.handlerPayResult(vo);
        return "success";
    }
}
