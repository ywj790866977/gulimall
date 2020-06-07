package com.yanlaoge.gulimall.auth.controller;

import cn.hutool.core.util.RandomUtil;
import com.yanlaoge.common.utils.RedisUtil;
import com.yanlaoge.common.utils.ResponseHelper;
import com.yanlaoge.common.utils.ResponseVo;
import com.yanlaoge.common.utils.ServiceAssert;
import com.yanlaoge.gulimall.auth.SmsStatusEnum;
import com.yanlaoge.gulimall.auth.constant.AuthServerconstant;
import com.yanlaoge.gulimall.auth.feign.ThridPartyFeignService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 *
 * @author rubyle
 * @date 2020-6-06
 */
@Controller
public class LoginContreller {

    @Resource
    private ThridPartyFeignService thridPartyFeignService;

    @Resource
    private RedisUtil redisUtil;

    @ResponseBody
    @GetMapping("/sms/sendcode")
    public ResponseVo sendCode(@RequestParam("phone") String phone){
        String value = (String) redisUtil.get(AuthServerconstant.SMS_PREFIX + phone);
        Long aLong = Long.valueOf(value.split("_")[1]);
        if(System.currentTimeMillis() - aLong < 60000){
            return ResponseHelper.error(SmsStatusEnum.SMS_CODE_EXCPETION.getCode(),
                    SmsStatusEnum.SMS_CODE_EXCPETION.getMsg());
        }

        String code = RandomUtil.randomNumbers(6);
        code = code + "_" + System.currentTimeMillis();
        redisUtil.set(AuthServerconstant.SMS_PREFIX + phone, code, 10 * 60);
        thridPartyFeignService.sendCode(phone,code);
        return ResponseHelper.success();
    }

}
