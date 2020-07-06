package com.yanlaoge.gulimall.thirdparty.controller;

import com.yanlaoge.common.utils.ResponseHelper;
import com.yanlaoge.common.utils.ResponseVo;
import com.yanlaoge.gulimall.thirdparty.utils.AlipayTemplate;
import com.yanlaoge.gulimall.thirdparty.vo.PayVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 好人
 * @date 2020-07-04 13:26
 **/
@RestController
@RequestMapping("/pay")
public class PayController {
    @Resource
    private AlipayTemplate alipayTemplate;

    /**
     * 支付宝
     *
     * @param vo vo
     * @return R
     */
    @PostMapping("/aliPay")
    public ResponseVo<String> aliPay(@RequestBody PayVo vo){
        try {
            return ResponseHelper.success(alipayTemplate.pay(vo));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseHelper.error("支付失败");
        }
    }

}
