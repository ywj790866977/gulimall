package com.yanlaoge.gulimall.member.feign;

import com.yanlaoge.common.utils.R;
import com.yanlaoge.common.utils.ResponseVo;
import com.yanlaoge.gulimall.member.entity.MemberEntity;
import com.yanlaoge.gulimall.member.entity.MemberReceiveAddressEntity;
import com.yanlaoge.gulimall.member.vo.MemberLoginVo;
import com.yanlaoge.gulimall.member.vo.MemberRegisterVo;
import com.yanlaoge.gulimall.member.vo.SocialUserVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author rubyle
 * @date 2020-06-07
 */
@FeignClient("gulimall-member")
@Component
public interface MemberFeignService {
    /**
     * 注册
     *
     * @param vo vo
     * @return R
     */
    @PostMapping("/member/member/register")
    ResponseVo<String> regist(@RequestBody MemberRegisterVo vo);

    /**
     * 登录
     *
     * @param vo vo
     * @return entity
     */
    @PostMapping("/member/member/login")
    ResponseVo<MemberEntity> login(@RequestBody MemberLoginVo vo);

    /**
     * 社交登录
     *
     * @param vo vo
     * @return r
     */
    @PostMapping("member/member/oauth/login")
    ResponseVo<MemberEntity> oauthLogin(@RequestBody SocialUserVo vo);

    /**
     * 获取地址列表
     *
     * @param memberId 用户
     * @return 集合
     */
    @GetMapping("member/memberreceiveaddress/{memberId}/addresses")
    ResponseVo<List<MemberReceiveAddressEntity>> getAddress(@PathVariable("memberId") Long memberId);

    /**
     * 根据id获取地址
     *
     * @param id 地址id
     * @return 地址
     */
    @RequestMapping("member/memberreceiveaddress/info/{id}")
    R info(@PathVariable("id") Long id);
}