package com.yanlaoge.gulimall.auth.enums;

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
    SMS_CODE_EXCPETION(10201,"验证码获取频率太高"),
    /**
     * 验证校验错误
     */
    SMS_CODE_NULL(10202,"验证校验错误");
    private Integer code;
    private String msg;
}
