package com.yanlaoge.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanlaoge.common.utils.*;
import com.yanlaoge.gulimall.order.entity.OrderEntity;
import com.yanlaoge.gulimall.order.feign.OrderFeignService;
import com.yanlaoge.gulimall.product.entity.SkuInfoEntity;
import com.yanlaoge.gulimall.product.feign.ProductFeignService;
import com.yanlaoge.gulimall.ware.constant.WareConsTant;
import com.yanlaoge.gulimall.ware.constant.WareRespStatus;
import com.yanlaoge.gulimall.ware.dao.WareSkuDao;
import com.yanlaoge.gulimall.ware.dto.SkuWareHasStockDto;
import com.yanlaoge.gulimall.ware.dto.StockLockDetailDto;
import com.yanlaoge.gulimall.ware.dto.StockLockedDto;
import com.yanlaoge.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.yanlaoge.gulimall.ware.entity.WareOrderTaskEntity;
import com.yanlaoge.gulimall.ware.entity.WareSkuEntity;
import com.yanlaoge.gulimall.ware.enums.OrderStatusEnum;
import com.yanlaoge.gulimall.ware.enums.WareStockStatusEnum;
import com.yanlaoge.gulimall.ware.service.WareOrderTaskDetailService;
import com.yanlaoge.gulimall.ware.service.WareOrderTaskService;
import com.yanlaoge.gulimall.ware.service.WareSkuService;
import com.yanlaoge.gulimall.ware.vo.OrderItemLockVo;
import com.yanlaoge.gulimall.ware.vo.SkuHasStockVo;
import com.yanlaoge.gulimall.ware.vo.WareSkuLockVo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


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
    @Resource
    private OrderFeignService orderFeignService;

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
        List<WareSkuEntity> list = this.list(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId)
                .eq("ware_id", wareId));
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
            if (CollectionUtils.isEmpty(wareIds)) {
                ResponseHelper.execption(WareRespStatus.STOCK_ERROR);
            }
            for (Long wareId : wareIds) {
                Long count = baseMapper.lockSkuStock(skuId, wareId, stockDto.getNum());
                if (count != 0) {
                    skuStocked = true;
                    // 保存锁定详情
                    WareOrderTaskDetailEntity detailEntity = new WareOrderTaskDetailEntity();
                    detailEntity.setSkuId(skuId).setSkuNum(stockDto.getNum()).setTaskId(taskEntity.getId())
                            .setWareId(wareId).setLockStatus(WareStockStatusEnum.LOCK.getCode()).setSkuName("");
                    wareOrderTaskDetailService.save(detailEntity);
                    // 发送锁定消息
                    StockLockDetailDto lockDetailDto = new StockLockDetailDto();
                    BeanUtils.copyProperties(detailEntity, lockDetailDto);
                    StockLockedDto lockedDto = StockLockedDto.builder().id(taskEntity.getId()).detailTo(lockDetailDto).build();
                    rabbitTemplate.convertAndSend(WareConsTant.STOC_KEVENT_EXCHANGE, WareConsTant.STOCK_LOCKED, lockedDto);
                    break;
                }
            }
            //如果有一个没锁成功就,抛出异常
            if (!skuStocked) {
                ResponseHelper.execption(WareRespStatus.STOCK_LOCK_ERROR);
            }
        }
        //锁定成功
        return true;
    }

    @SneakyThrows
    @Override
    public void releaseStockLock(StockLockedDto dto) {
        // 先查询任务单状态是否为,已锁定
        WareOrderTaskDetailEntity detailEntity = wareOrderTaskDetailService.getById(dto.getId());
        if (!WareStockStatusEnum.LOCK.getCode().equals(detailEntity.getLockStatus())) {
            return;
        }
        // 查询订单状态是否成功
        WareOrderTaskEntity taskEntity = wareOrderTaskService.getById(detailEntity.getTaskId());
        ResponseVo<OrderEntity> orderRes = orderFeignService.getOrder(taskEntity.getOrderSn());
        if (orderRes == null || orderRes.getCode() != 0) {
            log.error("[orderFeignService] remote getOrder method is error  res = {}", orderRes);
            ResponseHelper.execption(WareRespStatus.REMOTE_ERROR);
        }
        OrderEntity orderEntity = orderRes.getData();
        //订单不存在,或者为"已取消"状态, 都需要解锁
        if (orderEntity == null || OrderStatusEnum.CANCLED.getCode().equals(orderEntity.getStatus())) {
            unLockStock(detailEntity.getSkuId(), detailEntity.getWareId(), detailEntity.getSkuNum(), detailEntity.getId());
        }
    }

    /**
     * 防止订单服务处理过慢, 库存解锁消息,优先到期, 然后查询订单状态,导致不能解锁
     *
     * @param orderEntity 订单实体
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void releaseStockLock(OrderEntity orderEntity) {
        //查询工作单状态
        WareOrderTaskEntity taskEntity =  wareOrderTaskService.getOrderTaskByOrderSn(orderEntity.getOrderSn());
        //查询详情
        List<WareOrderTaskDetailEntity> detailEntities = wareOrderTaskDetailService.list(
                new QueryWrapper<WareOrderTaskDetailEntity>()
                        .eq("task_id", taskEntity.getId())
                        .eq("loack_status",WareStockStatusEnum.LOCK.getCode()));
        if(CollectionUtils.isEmpty(detailEntities)){
            for (WareOrderTaskDetailEntity detailEntity : detailEntities) {
                unLockStock(detailEntity.getSkuId(),detailEntity.getWareId(),detailEntity.getSkuNum(),taskEntity.getId());
            }
        }
    }

    /**
     * 解锁
     *
     * @param skuId        skuId
     * @param wareId       仓库id
     * @param num          数量
     * @param taskDetailId 任务详情id
     */
    private void unLockStock(Long skuId, Long wareId, Integer num, Long taskDetailId) {
        baseMapper.unLockStock(skuId, wareId, num);
        //更新工作单状态
        WareOrderTaskDetailEntity detailEntity = new WareOrderTaskDetailEntity();
        detailEntity.setId(taskDetailId).setLockStatus(WareStockStatusEnum.UN_LOCK.getCode());
        wareOrderTaskDetailService.updateById(detailEntity);
    }

}