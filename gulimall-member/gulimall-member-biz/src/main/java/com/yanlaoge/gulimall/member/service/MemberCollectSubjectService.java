package com.yanlaoge.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.gulimall.member.entity.MemberCollectSubjectEntity;

import java.util.Map;

/**
 * 会员收藏的专题活动
 *
 * @author rubyle
 * @date 2020-05-13 15:19:47
 */
public interface MemberCollectSubjectService extends IService<MemberCollectSubjectEntity> {
    /**
     * 分页查询
     *
     * @param params 分页参数
     * @return 分页
     */
    PageUtils queryPage(Map<String, Object> params);
}

