package com.yanlaoge.gulimall.order.web;

import com.yanlaoge.common.exception.ServiceException;
import com.yanlaoge.gulimall.order.constant.OrderRespStatus;
import com.yanlaoge.gulimall.order.service.OrderService;
import com.yanlaoge.gulimall.order.vo.OrderConfirmVo;
import com.yanlaoge.gulimall.order.vo.OrderSubmitVo;
import com.yanlaoge.gulimall.order.vo.SubmitOrderResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;

/**
 * @author 好人
 * @date 2020-06-14 12:03
 **/
@Controller
@Slf4j
public class OrderWebController {
    @Resource
    private OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrade(Model model) {
        OrderConfirmVo orderConfirmVo = orderService.confirmOrder();
        model.addAttribute("orderConfirmData", orderConfirmVo);
        return "confirm";
    }

    @PostMapping("/orderSubmit")
    public String submit(OrderSubmitVo vo, Model model, RedirectAttributes redirectAttributes) {
        try {
            SubmitOrderResponseVo res = orderService.orderSubmit(vo);
            model.addAttribute("submitOrderResp", res.getOrder());
            return "pay";
        } catch (ServiceException e) {
            log.error("[orderSubmit] is error ", e);
            redirectAttributes.addFlashAttribute("msg", ((ServiceException) e).getMsg());
        } catch (Exception e) {
            log.error("[orderSubmit] is error ", e);
            redirectAttributes.addFlashAttribute("msg", OrderRespStatus.ERROR.getMsg());
        }
        return "redirect:http://order.gulimall.com/toTrade";
    }
}