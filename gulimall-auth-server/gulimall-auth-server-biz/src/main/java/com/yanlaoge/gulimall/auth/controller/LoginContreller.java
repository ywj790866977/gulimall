package com.yanlaoge.gulimall.auth.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.Maps;
import com.yanlaoge.common.utils.RedisUtil;
import com.yanlaoge.common.utils.ResponseHelper;
import com.yanlaoge.common.utils.ResponseVo;
import com.yanlaoge.common.vo.MemberRespVo;
import com.yanlaoge.gulimall.auth.constant.AuthServerconstant;
import com.yanlaoge.gulimall.auth.enums.SmsStatusEnum;
import com.yanlaoge.gulimall.auth.vo.UserLoginVo;
import com.yanlaoge.gulimall.auth.vo.UserRegisterVo;
import com.yanlaoge.gulimall.member.entity.MemberEntity;
import com.yanlaoge.gulimall.member.feign.MemberFeignService;
import com.yanlaoge.gulimall.member.vo.MemberLoginVo;
import com.yanlaoge.gulimall.member.vo.MemberRegisterVo;
import com.yanlaoge.gulimall.thirdparty.feign.ThridPartyFeignService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author rubyle
 * @date 2020-6-06
 */
@Controller
@Slf4j
public class LoginContreller {

    @Resource
    private ThridPartyFeignService thridPartyFeignService;
    @Resource
    private MemberFeignService memberFeignService;
    @Resource
    private RedisUtil redisUtil;

    @ResponseBody
    @GetMapping("/sms/sendcode")
    public ResponseVo<String> sendCode(@RequestParam("phone") String phone) {
        String value = (String) redisUtil.get(AuthServerconstant.SMS_PREFIX + phone);
        if (!StringUtils.isEmpty(value) && System.currentTimeMillis() - Long.parseLong(value.split("_")[1])
                < 60000) {
            return ResponseHelper.error(SmsStatusEnum.SMS_CODE_EXCPETION.getCode(),
                    SmsStatusEnum.SMS_CODE_EXCPETION.getMsg());
        }


        String code = RandomUtil.randomNumbers(6);
        code = code + "_" + System.currentTimeMillis();
        redisUtil.set(AuthServerconstant.SMS_PREFIX + phone, code, 10 * 60);
        thridPartyFeignService.sendCode(phone, code);
        return ResponseHelper.success();
    }


    @PostMapping("/register")
    public String reg(@Validated UserRegisterVo vo, BindingResult res, RedirectAttributes model) {
        if (res.hasErrors()) {
            Map<String, String> errMap = res.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField,
                    FieldError::getDefaultMessage));
            model.addFlashAttribute("errors", errMap);
            //TODO: seesion共享数据, 但是在分布式环境中, session可能丢失
            return AuthServerconstant.REDIRECT_REG_DOMAIN;
        }
        //注册
        String code = vo.getCode();
        String[] s = code.split("_");
        String key = AuthServerconstant.SMS_PREFIX + vo.getPassword();
        String redisCode = (String) redisUtil.get(key);
        HashMap<String, String> errMap = MapUtil.newHashMap();
        if (StringUtils.isEmpty(redisCode) || !code.equals(s[0])) {
            errMap.put("code", SmsStatusEnum.SMS_CODE_NULL.getMsg());
            model.addFlashAttribute("errors", errMap);
            return AuthServerconstant.REDIRECT_REG_DOMAIN;
        }
        // 校验成功进行注册
        MemberRegisterVo memberRegisterVo = new MemberRegisterVo();
        BeanUtils.copyProperties(vo, memberRegisterVo);
        ResponseVo<String> responseVo = memberFeignService.regist(memberRegisterVo);
        if (responseVo.getCode() != 0) {
            log.error("[reg] error res:{}", responseVo);
            errMap.put("msg", responseVo.getMsg());
            model.addFlashAttribute("errors", errMap);
            return AuthServerconstant.REDIRECT_REG_DOMAIN;
        }
        // 删除
        redisUtil.del(key);
        return AuthServerconstant.REDIRECT_LOGIN_DOMAIN;
    }

    @PostMapping("login")
    public String login(UserLoginVo vo, RedirectAttributes attributes, HttpSession session) {
        MemberLoginVo loginVo = new MemberLoginVo();
        BeanUtil.copyProperties(vo, loginVo);
        ResponseVo<MemberEntity> responseVo = memberFeignService.login(loginVo);
        if (responseVo.getCode() != 0) {
            HashMap<String, String> errMap = Maps.newHashMap();
            errMap.put("msg", responseVo.getMsg());
            attributes.addFlashAttribute("errors", errMap);
            return AuthServerconstant.REDIRECT_LOGIN_DOMAIN;

        }
        MemberRespVo respVo = new MemberRespVo();
        BeanUtils.copyProperties(responseVo.getData(), respVo);
        session.setAttribute(AuthServerconstant.LOGIN_USER, respVo);
        return AuthServerconstant.REDIRECT_GULIMALL_DOMAIN;
    }

    @GetMapping("/login.html")
    public String loginPage(HttpSession session) {
        if (session.getAttribute(AuthServerconstant.LOGIN_USER) != null) {
            return AuthServerconstant.REDIRECT_GULIMALL_DOMAIN;
        }
        return "login";
    }
}
