package com.yanlaoge.gulimall.product.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.common.utils.Query;

import com.yanlaoge.gulimall.product.dao.ProductAttrValueDao;
import com.yanlaoge.gulimall.product.entity.ProductAttrValueEntity;
import com.yanlaoge.gulimall.product.service.ProductAttrValueService;
import org.springframework.transaction.annotation.Transactional;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

	@Override
	public void saveProductAttr(List<ProductAttrValueEntity> entities) {
		this.saveBatch(entities);
	}

    @Override
    public List<ProductAttrValueEntity> baseAttrListforspu(Long spuId) {

        return this.list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id",spuId));

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateBySpuid(Long spuId, List<ProductAttrValueEntity> entities) {
        // 1. 删除spu对应的属性
        baseMapper.delete(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id",spuId));

        // 2.添加
        entities.forEach(item->item.setSpuId(spuId));
        this.saveBatch(entities);

    }

}