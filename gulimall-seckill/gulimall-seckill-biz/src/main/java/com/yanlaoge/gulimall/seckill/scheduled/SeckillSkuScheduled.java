package com.yanlaoge.gulimall.seckill.scheduled;

import com.yanlaoge.gulimall.seckill.constant.SeckillConstant;
import com.yanlaoge.gulimall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author 好人
 * @date 2020-07-08 20:11
 **/
@Service
@Slf4j
public class SeckillSkuScheduled {
    @Resource
    private SeckillService seckillService;
    @Resource
    private RedissonClient redissonClient;

    @Scheduled(cron = "0 0/10 * * * ? ")
    public void uploadSeckillSkuLatest3Days(){
        log.info("[uploadSeckillSkuLatest3Days] 上架秒杀商品信息...");
        RLock lock = redissonClient.getLock(SeckillConstant.UPLOAD_LOCK);
        lock.lock(10, TimeUnit.SECONDS);
        try {
            seckillService.uploadSeckillSkuLatest3Days();
        }finally {
            lock.unlock();
        }
    }
}
