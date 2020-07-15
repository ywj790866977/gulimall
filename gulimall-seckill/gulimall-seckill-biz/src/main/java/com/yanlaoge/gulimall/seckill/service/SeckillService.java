package com.yanlaoge.gulimall.seckill.service;

import com.yanlaoge.gulimall.seckill.to.SeckillSkuRedisTo;

import java.util.List;

/**
 * @author 好人
 * @date 2020-07-08 20:16
 **/
public interface SeckillService {
    /**
     * 上架3天的秒杀商品
     */
    void uploadSeckillSkuLatest3Days();


    /**
     * 查询秒杀商品
     *
     * @return 集合
     */
    List<SeckillSkuRedisTo> getCurrentSeckillSkus();

    /**
     * 查询秒杀商品详细信息
     *
     * @param skuId skuid
     * @return to
     */
    SeckillSkuRedisTo getSkuSeckillInfo(Long skuId);

    /**
     * 秒杀
     *
     * @param killId 场次ID和商品ID
     * @param key    随机码
     * @param num    秒杀数量
     * @return 订单号
     */
    String kill(String killId, String key, Integer num);
}
