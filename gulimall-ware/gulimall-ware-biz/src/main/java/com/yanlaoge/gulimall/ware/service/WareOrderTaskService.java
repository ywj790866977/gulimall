package com.yanlaoge.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.gulimall.ware.entity.WareOrderTaskEntity;

import java.util.Map;

/**
 * 库存工作单
 *
 * @author rubyle
 * @date 2020-05-13 15:47:12
 */
public interface WareOrderTaskService extends IService<WareOrderTaskEntity> {
    /**
     * 分页查询
     *
     * @param params 分页参数
     * @return 分页
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查询工作单
     *
     * @param orderSn 订单号
     * @return 实体
     */
    WareOrderTaskEntity getOrderTaskByOrderSn(String orderSn);
}

