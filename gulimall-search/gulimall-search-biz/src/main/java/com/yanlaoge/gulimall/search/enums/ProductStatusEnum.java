package com.yanlaoge.gulimall.search.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 状态
 *
 * @author rubyle
 */
@AllArgsConstructor
@Getter
public enum ProductStatusEnum {
    /**
     * 新建
     */
    NEW_SPU(0,"新建"),
    /**
     * 上架
     */
    SPU_UP(1,"上架"),
    /**
     * 下架
     */
    SPU_DOWN(2,"下架"),
    /**
     * 商品保存异常
     */
    PRODUCT_UP_EXCEPTOPN(11000,"商品保存异常");
    private Integer code;
    private String msg;
}
