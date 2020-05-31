package com.yanlaoge.gulimall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * 检索vo
 *
 * @author rubyle
 */
@Data
public class SearchParamVo {
    /**
     * 搜索关键字
     */
    private String keyword;
    /**
     * 三级分类id
     */
    private Long catalog3Id;
    /**
     * 排序
     * sort=saleCount_asc/desc
     * sort=skuPrice_asc/desc
     * sort=hotScore_asc/desc
     */
    private String sort;
    /**
     * 是否有货
     * hasStock=0/1
     */
    private Integer hasStock;
    /**
     * 价格
     * skuPrice=1_500/_500/500_
     */
    private String skuPrice;
    /**
     * 品牌id
     * brandId=1
     */
    private List<Long> brandId;
    /**
     * 属性
     * attrs=2_5寸:6寸
     */
    private List<String> attrs;
    /**
     * 页码
     */
    private Integer pageNum;
}
