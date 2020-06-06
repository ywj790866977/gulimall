package com.yanlaoge.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.gulimall.product.entity.AttrGroupEntity;

import com.yanlaoge.gulimall.product.vo.AttrGroupWithAttrsVo;
import com.yanlaoge.gulimall.product.vo.SpuItemAttrGroupVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author rubyle
 * @date 2020-05-13 14:21:04
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    /**
     * 普通分页查询
     *
     * @param params 分页参数
     * @return 分页
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * catelogid 分页查询
     *
     * @param params    分页参数
     * @param catelogId catelogid
     * @return 分页
     */
    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    /**
     * 获取分类下所有分组&关联信息
     *
     * @param catelogId 分类id
     * @return 属性集合
     */
    List<AttrGroupWithAttrsVo> getAttrGroupwithAttrs(Long catelogId);

    /**
     * 根据spud获取
     *
     *
	 * @param catalogId 分类id
	 * @param spuId spuid
	 * @return 结合
     */
    List<SpuItemAttrGroupVo> getAttrGroupwithSpuId(Long spuId, Long catalogId);
}

