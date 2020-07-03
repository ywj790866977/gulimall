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
     * 已锁定
     */
    LOCK(1,"已锁定"),
    /**
     *已解锁
     */
    UN_LOCK(2,"已解锁"),
    /**
     * 已完成
     */
    SUCCESS(3,"已完成");


    private Integer code ;
    private String msg;
}
