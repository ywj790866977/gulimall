package com.yanlaoge.gulimall.search.vo;

import com.yanlaoge.gulimall.search.model.SkuModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 检索返回vo
 *
 * @author rubyle
 */
@Data
public class SearchResponseVo {
    /**
     * 结果集
     */
    private List<SkuModel> products;
    /**
     * 结果涉及品牌
     */
    private List<BrandVo> brands;
    /**
     * 属性
     */
    private List<AttrVo> attrs;
    /**
     * 分类
     */
    private List<CatalogVo> catalogs;
    /**
     * 面包屑导航
     */
    private List<NavVo> navs;
    /**
     * 已选择属性
     */
    private List<Long> attrIds = new ArrayList<>();
    private Integer pageNum;
    private Long total;
    private Integer totalPages;
}
