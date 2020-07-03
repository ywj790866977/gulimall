package com.yanlaoge.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.gulimall.order.entity.MqMessageEntity;

import java.util.Map;

/**
 * 
 *
 * @author rubyle
 * @date 2020-05-13 15:41:34
 */
public interface MqMessageService extends IService<MqMessageEntity> {
    /**
     * 分页查询
     *
     * @param params 分页参数
     * @return 分页
     */
    PageUtils queryPage(Map<String, Object> params);
}

