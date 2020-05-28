package com.yanlaoge.gulimall.product.service.impl;

import com.yanlaoge.gulimall.product.service.CategoryBrandRelationService;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import com.yanlaoge.gulimall.product.vo.Catalog2Vo;
import com.yanlaoge.gulimall.product.vo.Catalog3Vo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCascade(CategoryEntity category) {
        updateById(category);
        if(!StringUtils.isEmpty(category.getName())){
            categoryBrandRelationService.updateByCategoryId(category.getCatId(),category.getName());
            //TODO 其他冗余更新
        }
    }

    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        return this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid",0));
    }

    @Override
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        List<CategoryEntity> level1Categorys = getLevel1Categorys();
        return level1Categorys.stream().collect(Collectors.toMap(v->v.getCatId().toString(), this::getCatelog2Vos));
    }

    private List<Catalog2Vo> getCatelog2Vos(CategoryEntity entity) {
        List<CategoryEntity> entities = this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid",
                entity.getCatId()));
        List<Catalog2Vo> catalog2Vos = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(entities)) {
            catalog2Vos = entities.stream().map(item ->
                 new Catalog2Vo().setId(item.getCatId().toString()).setName(item.getName())
                        .setCatalog1Id(entity.getCatId().toString()).setCatalog3List(this.getCatelog3Vos(item))
            ).collect(Collectors.toList());
        }
        return catalog2Vos;
    }

    private List<Catalog3Vo> getCatelog3Vos(CategoryEntity entity) {
        List<CategoryEntity> entities = this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid",
                entity.getCatId()));
        List<Catalog3Vo> catalog3Vos = Lists.newArrayList();
        if(!CollectionUtils.isEmpty(entities)) {
            catalog3Vos = entities.stream().map(catelog3 ->
                    new Catalog3Vo().setCatalog2Id(entity.getCatId().toString()).setId(catelog3.getCatId().toString())
                            .setName(catelog3.getName())
            ).collect(Collectors.toList());
        }
        return catalog3Vos;
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