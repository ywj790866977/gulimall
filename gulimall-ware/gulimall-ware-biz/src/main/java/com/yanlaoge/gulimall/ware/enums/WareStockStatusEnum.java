package com.yanlaoge.gulimall.ware.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author rubyle
 */
@AllArgsConstructor
@Getter
public enum WareStockStatusEnum {
    /**
     * 商品无库存
     */
    NOT_STOCK(21001,"商品无库存"),
    /**
     *
     */
    STOCK_LOCK_ERROR(21002,"锁定库存失败");
    private Integer code ;
    private String msg;
}
