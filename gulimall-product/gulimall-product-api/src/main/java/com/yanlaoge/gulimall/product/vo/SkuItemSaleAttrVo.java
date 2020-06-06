package com.yanlaoge.gulimall.product.vo;

import lombok.Data;

import java.util.List;

/**
 * @author 好人
 * @date 2020-06-06 08:21
 **/
@Data
public class SkuItemSaleAttrVo {
    private Long attrId;
    private String attrName;
    private List<AttrValueWithSkuInfo> attrValues;
}
