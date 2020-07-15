package com.yanlaoge.gulimall.coupon.service.impl;

import com.yanlaoge.gulimall.coupon.entity.SeckillSkuRelationEntity;
import com.yanlaoge.gulimall.coupon.service.SeckillSkuRelationService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.common.utils.Query;

import com.yanlaoge.gulimall.coupon.dao.SeckillSessionDao;
import com.yanlaoge.gulimall.coupon.entity.SeckillSessionEntity;
import com.yanlaoge.gulimall.coupon.service.SeckillSessionService;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {

    @Resource
    private SeckillSkuRelationService seckillSkuRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SeckillSessionEntity> getLates3DaysSession() {
        //计算最近3天
        List<SeckillSessionEntity> seckillSessionEntities = this.list(
                new QueryWrapper<SeckillSessionEntity>().between("start_time",
                        fomartTime(LocalDate.now(), LocalTime.MIN),
                        fomartTime(LocalDate.now().plusDays(2), LocalTime.MAX)));
        if(!CollectionUtils.isEmpty(seckillSessionEntities)){
            return seckillSessionEntities.stream().peek(seckillSessionEntity -> {
                List<SeckillSkuRelationEntity> relationEntities = seckillSkuRelationService.list(
                        new QueryWrapper<SeckillSkuRelationEntity>().eq("promotion_session_id"
                                , seckillSessionEntity.getId()));
                seckillSessionEntity.setRelationSkus(relationEntities);
            }).collect(Collectors.toList());
        }
        return seckillSessionEntities;
    }

    private String fomartTime(LocalDate localDate, LocalTime max) {
        return LocalDateTime.of(localDate, max)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

}