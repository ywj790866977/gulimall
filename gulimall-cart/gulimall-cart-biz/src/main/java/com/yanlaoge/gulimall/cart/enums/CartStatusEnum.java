package com.yanlaoge.gulimall.cart.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 好人
 * @date 2020-06-14 14:55
 **/
@Getter
@AllArgsConstructor
public enum CartStatusEnum {
    /**
     * 查询商品价格异常
     */
    NOT_REMOTE_GETPRIC(13001,"查询商品价格异常"),
    /**
     * 查询商品价格异常
     */
    NOT_REMOTE_GETPRICE(13002,"查询商品价格异常");
    private Integer code;
    private String msg;
}
