package com.yanlaoge.gulimall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.yanlaoge.common.utils.HttpUtils;
import com.yanlaoge.common.utils.ResponseVo;
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

    @SneakyThrows
    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code){
        // 1. 获取 token
        HashMap<String, String> map = Maps.newHashMap();
        map.put("client_id","1734806084");
        map.put("client_secret","1a186d5b8056c3a41367c710b835a21c");
        map.put("grant_type","authorization_code");
        map.put("redirect_uri","http://auth.gulimall.com/oauth2.0/weibo/success");
        map.put("code",code);
        //https://api.weibo.com/oauth2/access_token?client_id=1734806084&client_secret=1a186d5b8056c3a41367c710b835a21c&grant_type=authorization_code&redirect_uri=http://auth.gulimall.com/oauth2.0/weibo/success&code=c154d5a71c29ec1729d5e91f2b58cae8
        HttpResponse response = HttpUtils.doPost(
                "https://api.weibo.com", "/oauth2/access_token", "post",new HashMap<String,String>(0),null,map);
        // 2. 跳转
        if(response.getStatusLine().getStatusCode() != 200){
            log.error("[weibo] oauth2.0  is err , res: {}",response);
            return "redirect:http://auth.gulimall.com/login.html";
        }
        String json = EntityUtils.toString(response.getEntity());
        SocialUser socialUser = JSON.parseObject(json, SocialUser.class);

        //自动注册
        SocialUserVo socialUserVo = new SocialUserVo();
        BeanUtils.copyProperties(socialUser,socialUserVo);
        ResponseVo<MemberEntity> responseVo = memberFeignService.oauthLogin(socialUserVo);
        if(responseVo.getCode() !=0){
            log.error("[weibo] oauth login is error , res:{}",responseVo);
        }

        return "redirect:http://gulimall.com";
    }
}
