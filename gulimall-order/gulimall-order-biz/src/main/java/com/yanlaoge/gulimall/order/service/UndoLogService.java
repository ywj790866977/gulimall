package com.yanlaoge.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.gulimall.order.entity.UndoLogEntity;

import java.util.Map;

/**
 * 
 *
 * @author rubyle
 * @email besokuser@163.com
 * @date 2020-05-13 15:41:34
 */
public interface UndoLogService extends IService<UndoLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

