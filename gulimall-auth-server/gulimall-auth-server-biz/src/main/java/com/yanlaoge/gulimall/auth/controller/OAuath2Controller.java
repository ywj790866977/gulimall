package com.yanlaoge.gulimall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.yanlaoge.common.utils.HttpUtils;
import com.yanlaoge.gulimall.auth.vo.SocialUser;
import lombok.SneakyThrows;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;

/**
 * @author 好人
 * @date 2020-06-08 09:32
 **/
public class OAuath2Controller {

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
        //https://api.weibo.com/oauth2/access_token?client_id=YOUR_CLIENT_ID&client_secret=YOUR_CLIENT_SECRET&grant_type=authorization_code&redirect_uri=YOUR_REGISTERED_REDIRECT_URI&code=CODE
        HttpResponse response = HttpUtils.doPost("api.weibo.com", "/oauth2/access_token", "post", null, null, map);
        // 2. 跳转
        if(response.getStatusLine().getStatusCode() != 200){
            return "redirect:http://auth.gulimall.com/login.html";
        }
        String json = EntityUtils.toString(response.getEntity());
        SocialUser socialUser = JSON.parseObject(json, SocialUser.class);

        return "redirect:http://gulimall.com";
    }
}
