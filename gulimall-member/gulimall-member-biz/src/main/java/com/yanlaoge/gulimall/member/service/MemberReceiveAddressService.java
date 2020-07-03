package com.yanlaoge.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.gulimall.member.entity.MemberReceiveAddressEntity;

import java.util.List;
import java.util.Map;

/**
 * 会员收货地址
 *
 * @author rubyle
 * @date 2020-05-13 15:19:47
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddressEntity> {
    /**
     * 分页查询
     *
     * @param params 分页参数
     * @return 分页
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取用户的地址
     *
     * @param memberId 用户id
     * @return 集合
     */
    List<MemberReceiveAddressEntity> getAddress(Long memberId);
}

