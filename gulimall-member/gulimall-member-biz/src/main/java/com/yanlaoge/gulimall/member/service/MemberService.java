package com.yanlaoge.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.gulimall.member.entity.MemberEntity;
import com.yanlaoge.gulimall.member.vo.MemberRegisterVo;

import java.util.Map;

/**
 * 会员
 *
 * @author rubyle
 * @email besokuser@163.com
 * @date 2020-05-13 15:19:47
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 注册
     *
     * @param vo vo
     */
    void register(MemberRegisterVo vo);

    /**
     * 检查email是否唯一
     *
     * @param email email地址
     * @return bool
     */
    boolean checkEmailUnique(String email);

    /**
     * 检查手机号
     *
     * @param phone 手机号
     */
    void checkPhoneUnique(String phone);

    /**
     * 用户名
     *
     * @param userName username
     */
    void checkUserNameUnique(String userName);


}

