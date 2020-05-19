package com.yanlaoge.gulimall.product.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.common.utils.Query;

import com.yanlaoge.gulimall.product.dao.CategoryDao;
import com.yanlaoge.gulimall.product.entity.CategoryEntity;
import com.yanlaoge.gulimall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listByTree() {
        //1.
        List<CategoryEntity> categoryEntities = list();
        //2
        return categoryEntities.stream()
                .peek(menu -> menu.setChildren(getChildren(menu, categoryEntities)))
                .filter(item -> item.getParentCid() == 0)
                .sorted(((o1, o2) -> (o1.getSort() == null ? 0 : o1.getSort()) - (o2.getSort() == null ? 0 :o2.getSort())))
                .collect(Collectors.toList());
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 业务判断
        removeByIds(asList);
//        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public List<Long> findCatelogIds(Long catelogId) {
        ArrayList<Long> list = Lists.newArrayList();
        findParentPath(catelogId,list);
//        findParentPathByWhile(catelogId,list);
        return Lists.reverse(list) ;
    }

    private void findParentPathByWhile(Long catelogId, ArrayList<Long> list) {
        list.add(catelogId);
        CategoryEntity categoryEntity = this.getById(catelogId);
        while (categoryEntity.getParentCid() != null && categoryEntity.getParentCid() != 0){
            list.add(categoryEntity.getParentCid());
            categoryEntity = this.getById(categoryEntity.getParentCid());
        }
    }

    private void findParentPath(Long catelogId, ArrayList<Long> list) {
        list.add(catelogId);
        CategoryEntity categoryEntity = getById(catelogId);
        if(categoryEntity.getParentCid() != null && categoryEntity.getParentCid()!=0){
            findParentPath(categoryEntity.getParentCid(),list);
        }
    }

    public List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> list) {
        return list.stream()
                .filter(item -> item.getParentCid().equals(root.getCatId()))
                .peek(item -> item.setChildren(getChildren(item, list)))
                .sorted(((o1, o2) -> (o1.getSort() == null ? 0 : o1.getSort()) - (o2.getSort() == null ? 0 :o2.getSort())))
//                .sorted(Comparator.comparingInt(CategoryEntity::getSort))
                .collect(Collectors.toList());
    }

}