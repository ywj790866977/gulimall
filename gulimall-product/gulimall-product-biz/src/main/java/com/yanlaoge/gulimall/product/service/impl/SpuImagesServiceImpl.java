package com.yanlaoge.gulimall.product.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.common.utils.Query;

import com.yanlaoge.gulimall.product.dao.SpuImagesDao;
import com.yanlaoge.gulimall.product.entity.SpuImagesEntity;
import com.yanlaoge.gulimall.product.service.SpuImagesService;
import org.springframework.util.CollectionUtils;


@Service("spuImagesService")
public class SpuImagesServiceImpl extends ServiceImpl<SpuImagesDao, SpuImagesEntity> implements SpuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuImagesEntity> page = this.page(
                new Query<SpuImagesEntity>().getPage(params),
                new QueryWrapper<SpuImagesEntity>()
        );

        return new PageUtils(page);
    }

	@Override
	public void saveSpuImages(Long id, List<String> images) {
		if(!CollectionUtils.isEmpty(images)){
			List<SpuImagesEntity> collect = images.stream().map(item -> {
				SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
				spuImagesEntity.setSpuId(id);
				spuImagesEntity.setImgUrl(item);
				return spuImagesEntity;
			}).collect(Collectors.toList());
			this.saveBatch(collect);
		}
	}

}