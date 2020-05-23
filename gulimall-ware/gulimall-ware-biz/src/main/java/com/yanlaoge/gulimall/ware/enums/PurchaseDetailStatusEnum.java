package com.yanlaoge.gulimall.ware.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 好人
 * @date 2020-05-22 10:27
 **/
@AllArgsConstructor
@Getter
public enum PurchaseDetailStatusEnum {
    /**
     * 创建
     */
    CREATED(0,"新建"),
    /**
     * 已分配
     */
    ASSIGNED(1,"已分配"),
    /**
     * 已领取
     */
    RECEIVE(2,"正在采购"),
    /**
     * 已完成
     */
    FINISH(3,"已完成"),
    /**
     * 有异常
     */
    HASERROR(4,"采购失败");

    private Integer code;
    private String msg;
}
