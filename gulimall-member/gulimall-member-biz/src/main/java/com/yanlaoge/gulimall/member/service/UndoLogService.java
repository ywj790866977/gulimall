package com.yanlaoge.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.gulimall.member.entity.UndoLogEntity;

import java.util.Map;

/**
 * 
 *
 * @author rubyle
 * @date 2020-05-13 15:19:47
 */
public interface UndoLogService extends IService<UndoLogEntity> {
    /**
     * 分页查询
     *
     * @param params 分页参数
     * @return 分页
     */
    PageUtils queryPage(Map<String, Object> params);
}

