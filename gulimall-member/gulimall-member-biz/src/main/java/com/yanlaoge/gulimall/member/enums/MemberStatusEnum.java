package com.yanlaoge.gulimall.member.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author rubyle
 * @date 2020-06-07
 */
@Getter
@AllArgsConstructor
public enum MemberStatusEnum {
    /**
     * 用户名已存在
     */
    NOT_USERNAME(15001,"用户名已存在"),
    /**
     * 手机号已存在
     */
    NOT_PHONE(15002,"手机号已存在");
    private Integer code;
    private String msg;
}
