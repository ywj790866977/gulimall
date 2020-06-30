package com.yanlaoge.gulimall.ware.dto;

import lombok.Data;

import java.util.List;

/**
 * @author  rubyle
 */
@Data
public class SkuWareHasStockDto {
    private Long skuId;
    private Integer num;
    private List<Long> wareId;
}
