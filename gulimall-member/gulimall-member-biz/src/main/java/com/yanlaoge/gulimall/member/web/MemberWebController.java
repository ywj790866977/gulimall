package com.yanlaoge.gulimall.member.web;

import com.google.common.collect.Maps;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.common.utils.ResponseVo;
import com.yanlaoge.gulimall.order.feign.OrderFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author 好人
 * @date 2020-07-04 17:50
 **/
@Controller
@Slf4j
public class MemberWebController {

    @Resource
    private OrderFeignService orderFeignService;

    @GetMapping("/memberOrder.html")
    public String memberOrderPage(@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                  @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize, Model model){
        //验证支付宝给我传来的所有请求数据
        Map<String,Object> params = Maps.newHashMap();
        params.put("page",pageNum.toString());
        params.put("limit",pageSize.toString());
        ResponseVo<PageUtils> responseVo = orderFeignService.listWithItem(params);
        if(responseVo == null || responseVo.getCode() !=0){
            log.error("[memberOrderPage] remote  method listWithItem is error res : {}",responseVo);

        }
        model.addAttribute("orders",responseVo.getData());
        return "orderList";
    }
}
