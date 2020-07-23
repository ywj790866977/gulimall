package com.yanlaoge.gulimall.sso.controller;

import com.yanlaoge.common.utils.ResponseHelper;
import com.yanlaoge.common.utils.ResponseVo;
import com.yanlaoge.gulimall.sso.service.AuthService;
import com.yanlaoge.gulimall.sso.util.AuthToken;
import com.yanlaoge.gulimall.sso.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * @author rubyle
 * @date 2020/07/23
 */
@RestController
@RequestMapping(value = "/userx")
public class AuthController {

    /**
     * 客户端ID
     */
    @Value("${auth.clientId}")
    private String clientId;

    /**
     * 秘钥
     */
    @Value("${auth.clientSecret}")
    private String clientSecret;

    /**
     * Cookie存储的域名
     */
    @Value("${auth.cookieDomain}")
    private String cookieDomain;

    /**
     * Cookie生命周期
     */
    @Value("${auth.cookieMaxAge}")
    private int cookieMaxAge;

    @Resource
    AuthService authService;

    @PostMapping("/login")
    public ResponseVo<String> login(String username, String password) {
        if(StringUtils.isEmpty(username)){
            throw new RuntimeException("用户名不允许为空");
        }
        if(StringUtils.isEmpty(password)){
            throw new RuntimeException("密码不允许为空");
        }
        //申请令牌
        AuthToken authToken =  authService.login(username,password,clientId,clientSecret);

        //用户身份令牌
        String accessToken = authToken.getAccessToken();
        //将令牌存储到cookie
        saveCookie(accessToken);
        return ResponseHelper.success("登录成功");
    }

    /***
     * 将令牌存储到cookie
     * @param token
     */
    private void saveCookie(String token){
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        CookieUtil.addCookie(response,cookieDomain,"/","Authorization",token,cookieMaxAge,false);
    }
}
