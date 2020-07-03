package com.yanlaoge.gulimall.ware.service.impl;

import com.yanlaoge.common.utils.R;
import com.yanlaoge.common.utils.ResponseHelper;
import com.yanlaoge.gulimall.product.entity.SkuInfoEntity;
import com.yanlaoge.gulimall.product.feign.ProductFeignService;
import com.yanlaoge.gulimall.ware.constant.WareConsTant;
import com.yanlaoge.gulimall.ware.dto.SkuWareHasStockDto;
import com.yanlaoge.gulimall.ware.dto.StockLockedDto;
import com.yanlaoge.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.yanlaoge.gulimall.ware.entity.WareOrderTaskEntity;
import com.yanlaoge.gulimall.ware.enums.WareStockStatusEnum;
import com.yanlaoge.gulimall.ware.service.WareOrderTaskDetailService;
import com.yanlaoge.gulimall.ware.service.WareOrderTaskService;
import com.yanlaoge.gulimall.ware.vo.OrderItemLockVo;
import com.yanlaoge.gulimall.ware.vo.SkuHasStockVo;
import com.yanlaoge.gulimall.ware.vo.WareSkuLockVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;


/**
 * @author rubyle
 */
@Service("wareSkuService")
@Slf4j
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Resource
    private ProductFeignService productFeignService;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private WareOrderTaskDetailService wareOrderTaskDetailService;
    @Resource
    private WareOrderTaskService wareOrderTaskService;

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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean orderLockStock(WareSkuLockVo vo) {
        // TODO 按照下单送货地址,找就近仓库
        // 1.保存库存工作单
        WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
        taskEntity.setOrderSn(vo.getOrderSn());
        wareOrderTaskService.save(taskEntity);


        // 找到每个商品在哪个仓库有库存
        List<OrderItemLockVo> locks = vo.getLocks();
        List<SkuWareHasStockDto> collect = locks.stream().map(item -> {
            SkuWareHasStockDto skuWareHasStockDto = new SkuWareHasStockDto();
            Long skuId = item.getSkuId();
            skuWareHasStockDto.setSkuId(skuId);
            skuWareHasStockDto.setNum(item.getCount());
            // 查询仓库
            List<Long> wareIds = baseMapper.queryWaresBySku(skuId);
            skuWareHasStockDto.setWareId(wareIds);
            return skuWareHasStockDto;
        }).collect(Collectors.toList());

        //锁定
        for (SkuWareHasStockDto stockDto : collect) {
            boolean skuStocked = false;
            Long skuId = stockDto.getSkuId();
            List<Long> wareIds = stockDto.getWareId();
            if(CollectionUtils.isEmpty(wareIds)){
                ResponseHelper.execption(WareStockStatusEnum.NOT_STOCK.getCode(),WareStockStatusEnum.NOT_STOCK.getMsg());
            }
            for (Long wareId : wareIds) {
                Long count = baseMapper.lockSkuStock(skuId,wareId,stockDto.getNum());
                if(count != 0){
                    skuStocked = true;
                    // 保存锁定详情
                    WareOrderTaskDetailEntity detailEntity = new WareOrderTaskDetailEntity();
                    detailEntity.setSkuId(skuId).setSkuNum(stockDto.getNum()).setTaskId(taskEntity.getId())
                            .setWareId(wareId).setLockStatus(1).setSkuName("");
                    wareOrderTaskDetailService.save(detailEntity);
                    StockLockedDto stockLockedDto = new StockLockedDto();
                    stockLockedDto.setId(taskEntity.getId());
                    stockLockedDto.setDetailId(detailEntity.getId());
                    rabbitTemplate.convertAndSend(WareConsTant.STOC_KEVENT_EXCHANGE,WareConsTant.STOCK_LOCKED,stockLockedDto);
                    break;
                }
            }
            //如果有一个没锁成功就,抛出异常
            if(!skuStocked){
                ResponseHelper.execption(WareStockStatusEnum.STOCK_LOCK_ERROR.getCode(),
                    WareStockStatusEnum.STOCK_LOCK_ERROR.getMsg());
            }
        }
        //锁定成功
        return true;
    }

}