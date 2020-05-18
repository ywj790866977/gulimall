package com.yanlaoge.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.gulimall.product.entity.AttrGroupEntity;

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
}

