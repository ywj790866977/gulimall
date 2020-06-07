package com.yanlaoge.gulimall.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 好人
 * @date 2020-06-07 09:48
 **/
@Getter
@AllArgsConstructor
public enum SmsStatusEnum {
    /**
     * 验证码获取频率太高
     */
    SMS_CODE_EXCPETION(10003,"验证码获取频率太高");
    private Integer code;
    private String msg;
}
