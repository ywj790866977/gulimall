package com.yanlaoge.gulimall.ware.service.impl;

import com.yanlaoge.common.utils.R;
import com.yanlaoge.gulimall.product.entity.SkuInfoEntity;
import com.yanlaoge.gulimall.ware.feign.ProductFeignService;
import com.yanlaoge.gulimall.ware.vo.SkuHasStockVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.common.utils.Query;

import com.yanlaoge.gulimall.ware.dao.WareSkuDao;
import com.yanlaoge.gulimall.ware.entity.WareSkuEntity;
import com.yanlaoge.gulimall.ware.service.WareSkuService;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;


@Service("wareSkuService")
@Slf4j
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Resource
    private ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId)) {
            wrapper.eq("sku_id", skuId);
        }
        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId)) {
            wrapper.eq("ware_id", wareId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        List<WareSkuEntity> list = this.list(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id",
                wareId));
        if (!CollectionUtils.isEmpty(list)) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            //TODO 其他方式处理
            R r = null;
            try {
                r = productFeignService.info(skuId);
                if (r.getMapCode() != 0) {
                    log.error("【addStock】调用服务错误 r:{}", r);
                }
                SkuInfoEntity skuInfo = (SkuInfoEntity) r.get("skuInfo");
                wareSkuEntity.setSkuName(skuInfo.getSkuName());
            } catch (Exception e) {
                log.error("【addStock】调用服务错误 ", e);
            }
            this.save(wareSkuEntity);
        } else {
            baseMapper.addStock(skuId, wareId, skuNum);
        }
    }

    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        return skuIds.stream().map(skuId -> {
            SkuHasStockVo skuHasStockVo = new SkuHasStockVo();
            Long stock = baseMapper.getSkuStock(skuId);
            skuHasStockVo.setHasStock(Optional.ofNullable(stock).orElse(0L) > 0);
            skuHasStockVo.setSkuId(skuId);
            return skuHasStockVo;
        }).collect(Collectors.toList());
    }

}