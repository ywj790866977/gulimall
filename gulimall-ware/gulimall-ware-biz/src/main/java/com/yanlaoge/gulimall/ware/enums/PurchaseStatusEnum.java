package com.yanlaoge.gulimall.ware.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 好人
 * @date 2020-05-22 10:27
 **/
@AllArgsConstructor
@Getter
public enum  PurchaseStatusEnum {
    /**
     * 创建
     */
    CREATED(0,"创建"),
    /**
     * 已分配
     */
    ASSIGNED(1,"已分配"),
    /**
     * 已领取
     */
    RECEIVE(2,"已领取"),
    /**
     * 已完成
     */
    FINISH(3,"已完成"),
    /**
     * 有异常
     */
    HASERROR(4,"有异常");

    private Integer code;
    private String msg;
}
