package com.yanlaoge.gulimall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.yanlaoge.common.utils.HttpUtils;
import com.yanlaoge.common.utils.ResponseVo;
import com.yanlaoge.common.vo.MemberRespVo;
import com.yanlaoge.gulimall.auth.config.WeiboConfigProperties;
import com.yanlaoge.gulimall.auth.constant.AuthServerconstant;
import com.yanlaoge.gulimall.auth.vo.SocialUser;
import com.yanlaoge.gulimall.member.entity.MemberEntity;
import com.yanlaoge.gulimall.member.feign.MemberFeignService;
import com.yanlaoge.gulimall.member.vo.SocialUserVo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;

/**
 * @author 好人
 * @date 2020-06-08 09:32
 **/
@Controller
@Slf4j
public class OAuath2Controller {

    @Resource
    private MemberFeignService memberFeignService;
    @Resource
    private WeiboConfigProperties weiboConfigProperties;

    @SneakyThrows
    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession session){
        // 1. 获取 token
        System.out.println(weiboConfigProperties.getClientId());
        HttpResponse response = getWeiboResp(code);
        // 2. 跳转
        if(!AuthServerconstant.HTTP_SUCCESS_CODE.equals(response.getStatusLine().getStatusCode())){
            log.error("[weibo] oauth2.0  is err , res: {}",response);
            return "redirect:http://auth.gulimall.com/login.html";
        }
        String json = EntityUtils.toString(response.getEntity());
        SocialUser socialUser = JSON.parseObject(json, SocialUser.class);
        //自动注册
        SocialUserVo socialUserVo = new SocialUserVo();
        BeanUtils.copyProperties(socialUser,socialUserVo);
        ResponseVo<MemberEntity> responseVo = memberFeignService.oauthLogin(socialUserVo);
        if(!AuthServerconstant.SERVICE_SUCCESS_CODE.equals(responseVo.getCode())){
            log.error("[weibo] oauth login is error , res:{}",responseVo);
        }
        // 用户信息放入Redis session
        MemberRespVo respVo = new MemberRespVo();
        BeanUtils.copyProperties(responseVo.getData(),respVo);
        session.setAttribute(AuthServerconstant.LOGIN_USER,respVo);
        return "redirect:http://gulimall.com";
    }

    private HttpResponse getWeiboResp(@RequestParam("code") String code) throws Exception {
        HashMap<String, String> map = Maps.newHashMap();
        map.put("client_id",weiboConfigProperties.getClientId());
        map.put("client_secret",weiboConfigProperties.getClientSecret());
        map.put("grant_type","authorization_code");
        map.put("redirect_uri",weiboConfigProperties.getRedirectUri());
        map.put("code",code);
        return HttpUtils.doPost(
                weiboConfigProperties.getHost(), weiboConfigProperties.getPath(),
                "post",new HashMap<String,String>(0),null, map);
    }
}
