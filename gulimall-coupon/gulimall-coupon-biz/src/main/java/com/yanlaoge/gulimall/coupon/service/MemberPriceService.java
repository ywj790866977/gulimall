package com.yanlaoge.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.gulimall.coupon.entity.MemberPriceEntity;

import java.util.Map;

/**
 * 商品会员价格
 *
 * @author rubyle
 * @date 2020-05-13 15:02:05
 */
public interface MemberPriceService extends IService<MemberPriceEntity> {
    /**
     * 分页查询
     *
     * @param params 分页参数
     * @return 分页
     */
    PageUtils queryPage(Map<String, Object> params);
}

