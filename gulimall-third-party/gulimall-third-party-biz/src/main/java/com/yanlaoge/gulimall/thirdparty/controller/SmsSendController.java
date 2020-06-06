package com.yanlaoge.gulimall.thirdparty.controller;

import com.yanlaoge.common.utils.ResponseHelper;
import com.yanlaoge.common.utils.ResponseVo;
import com.yanlaoge.gulimall.thirdparty.component.SmsComponent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author rubyle
 * @date 2020-06-06
 */
@RestController
@RequestMapping("/sms")
public class SmsSendController {
    @Resource
    private SmsComponent smsComponent;

    @GetMapping
    public ResponseVo<String> sendCode(@RequestParam("phone") String phone , @RequestParam("code")  String code){
        smsComponent.sendSmsCode(phone,code);
        return ResponseHelper.success();
    }
}
