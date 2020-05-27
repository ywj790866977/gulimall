package com.yanlaoge.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.gulimall.product.entity.AttrEntity;

import com.yanlaoge.gulimall.product.vo.AttrGroupRelationVo;
import com.yanlaoge.gulimall.product.vo.AttrResVo;
import com.yanlaoge.gulimall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author rubyle
 * @date 2020-05-13 14:21:04
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存vo
     *
     * @param attrVo vo
     */
    void saveAttr(AttrVo attrVo);

    /**
     * 分页条件查询
     *
     * @param params    分页参数
     * @param catelogId 三级分类id
     * @param type
     * @return page
     */
    PageUtils queryBaseAttrList(Map<String, Object> params, Long catelogId, String type);

    /**
     * 获取信息
     *
     * @param attrId attid
     * @return 实体
     */
    AttrResVo getAttrInfo(Long attrId);

    /**
     * 更新attr
     *
     * @param attr attr
     */
    void updateAttr(AttrVo attr);

    /**
     * 根据分组id查询所有属性
     *
     * @param attrgroupId groupId
     * @return 集合
     */
    List<AttrEntity> getRelationAttr(Long attrgroupId);

    /**
     * 根据vos删除
     *
     * @param relationVos vos
     */
    void deleteRelation(List<AttrGroupRelationVo> relationVos);

    /**
     * 查找未关联属性
     *
     * @param params      分页参数
     * @param attrgroupId 当前组id
     * @return 属性
     */
    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);

    /**
     * 检索 属性
     *
     * @param attrIds attrids
     * @return 检索集合
     */
    List<Long> selectSearchAttr(List<Long> attrIds);
}

