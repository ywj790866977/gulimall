package com.yanlaoge.gulimall.sso.controller;

import com.yanlaoge.gulimall.sso.service.LoginService;
import com.yanlaoge.gulimall.sso.util.AuthToken;
import com.yanlaoge.gulimall.sso.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author rubyle
 * @date 2020/07/23
 */
@RestController
@RequestMapping("/user")
public class UserLoginController {

    @Resource
    private LoginService loginService;

    @Value("${auth.clientId}")
    private String clientId;

    @Value("${auth.clientSecret}")
    private String clientSecret;

    /**
     * 授权模式 密码模式
     */
    private static final String GRAND_TYPE = "password";


    @Value("${auth.cookieDomain}")
    private String cookieDomain;

    /**
     * Cookie生命周期
     */
    @Value("${auth.cookieMaxAge}")
    private int cookieMaxAge;


    /**
     * 密码模式  认证.
     *
     * @param username 用户名
     * @param password 密码
     * @return
     */
    @RequestMapping("/login")
    public void login(String username, String password) {
        //登录 之后生成令牌的数据返回
        AuthToken authToken = loginService.login(username, password, clientId, clientSecret, GRAND_TYPE);


        //设置到cookie中
        saveCookie(authToken.getAccessToken());
//        return new Result<>(true, StatusCode.OK,"令牌生成成功",authToken);
    }

    private void saveCookie(String token){
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        CookieUtil.addCookie(response,cookieDomain,"/","Authorization",token,cookieMaxAge,false);
    }
}
