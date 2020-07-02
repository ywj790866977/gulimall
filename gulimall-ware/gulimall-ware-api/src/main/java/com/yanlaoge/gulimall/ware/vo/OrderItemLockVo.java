package com.yanlaoge.gulimall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 好人
 * @date 2020-06-14 13:44
 **/
@Data
public class OrderItemLockVo {
    private Long skuId;
    private String title;
    private String image;
    private List<String> skuAttr;
    private BigDecimal price;
    private Integer count;
    private BigDecimal totalPrice;
    private Boolean hasStock;
    private BigDecimal weight;

    public BigDecimal getTotalPrice() {
        if(this.price!=null){
            return this.price.multiply(new BigDecimal(String.valueOf(this.count)));
        }
        return BigDecimal.ZERO;
    }
}
