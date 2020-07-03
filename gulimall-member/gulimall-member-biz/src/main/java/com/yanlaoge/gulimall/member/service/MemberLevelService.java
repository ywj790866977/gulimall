package com.yanlaoge.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.gulimall.member.entity.MemberLevelEntity;

import java.util.Map;

/**
 * 会员等级
 *
 * @author rubyle
 * @date 2020-05-13 15:19:47
 */
public interface MemberLevelService extends IService<MemberLevelEntity> {
    /**
     * 分页查询
     *
     * @param params 分页参数
     * @return 分页
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查询用户默认等级
     *
     * @return enti
     */
    MemberLevelEntity getDefaultLevel();
}

