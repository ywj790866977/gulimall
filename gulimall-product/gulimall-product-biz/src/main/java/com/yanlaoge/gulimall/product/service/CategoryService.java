package com.yanlaoge.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.gulimall.product.entity.CategoryEntity;
import com.yanlaoge.gulimall.product.vo.Catalog2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author rubyle
 * @date 2020-05-13 14:21:04
 */
public interface CategoryService extends IService<CategoryEntity> {
    /**
     * 分页查询
     *
     * @param params 分页参数
     * @return 分页
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查询分级结构
     *
     * @return 树形集合
     */
    List<CategoryEntity> listByTree();

    /**
     * 根据ids删除
     *
     * @param asList 删除id数组
     */
    void removeMenuByIds(List<Long> asList);

    /**
     * 根据catelogId查找完整路径
     *
     * @param catelogId catelogId
     * @return 路径集合
     */
    List<Long> findCatelogIds(Long catelogId);

    /**
     * 级联更新关联的所有数据
     *
     * @param category 实体
     */
    void updateCascade(CategoryEntity category);

    /**
     * 查询一级分类
     *
     * @return 分类集合
     */
    List<CategoryEntity> getLevel1Categorys();

    /**
     * 返回分类
     *
     * @return 分类
     */
    Map<String, List<Catalog2Vo>> getCatalogJson();

}

