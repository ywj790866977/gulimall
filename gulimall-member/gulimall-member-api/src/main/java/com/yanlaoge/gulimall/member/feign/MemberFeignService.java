package com.yanlaoge.gulimall.member.feign;

import com.yanlaoge.common.utils.ResponseVo;
import com.yanlaoge.gulimall.member.vo.MemberRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
}