package com.yanlaoge.gulimall.ware.vo;

import lombok.Data;

/**
 * @author 好人
 * @date 2020-05-22 12:05
 **/
@Data
public class PurchaseDoneItemVo {
    /**
     * itemid
     */
    private Long itemId;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 原因
     */
    private String reason;
}
