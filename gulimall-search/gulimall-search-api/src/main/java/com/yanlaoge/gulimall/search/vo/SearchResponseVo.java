package com.yanlaoge.gulimall.search.vo;

import com.yanlaoge.gulimall.search.model.SpuModel;
import lombok.Data;

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
    private List<SpuModel> products;
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

    private Integer pageNum;
    private Long total;
    private Integer totalPages;
}
