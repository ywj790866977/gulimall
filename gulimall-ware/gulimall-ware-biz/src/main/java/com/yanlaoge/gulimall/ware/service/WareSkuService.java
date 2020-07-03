package com.yanlaoge.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rabbitmq.client.Channel;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.gulimall.ware.dto.StockLockedDto;
import com.yanlaoge.gulimall.ware.entity.WareSkuEntity;
import com.yanlaoge.gulimall.ware.vo.SkuHasStockVo;
import com.yanlaoge.gulimall.ware.vo.WareSkuLockVo;
import org.springframework.amqp.core.Message;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author rubyle
 * @date 2020-05-13 15:47:12
 */
public interface WareSkuService extends IService<WareSkuEntity> {
    /**
     * 分页查询
     *
     * @param params 分页参数
     * @return 分页
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 库存
     *
     * @param skuId  skuid
     * @param wareId 仓库id
     * @param skuNum 熟练
     */
    void addStock(Long skuId, Long wareId, Integer skuNum);

    /**
     * 检查商品库存
     *
     * @param skuIds 商品id
     * @return 库存信息
     */
    List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds);

    /**
     * 锁定库存
     *
     * @param vo vo
     * @return 锁定结果vo
     */
    Boolean orderLockStock(WareSkuLockVo vo);

    /**
     * 释放库存
     *
     * @param dto dto
     */
    void releaseStockLock(StockLockedDto dto);

}

