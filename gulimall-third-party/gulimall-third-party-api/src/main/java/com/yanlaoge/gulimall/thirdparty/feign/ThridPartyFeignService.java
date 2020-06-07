package com.yanlaoge.gulimall.thirdparty.feign;

import com.yanlaoge.common.utils.ResponseVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 好人
 * @date 2020-06-06 23:11
 **/
@FeignClient("gulimall-third-party")
public interface ThridPartyFeignService {

    /**
     * 发送验证码
     *
     * @param phone 手机号
     * @param code  验证码
     * @return R
     */
    @GetMapping("/sms/sendcode")
    ResponseVo<String> sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code);
}
