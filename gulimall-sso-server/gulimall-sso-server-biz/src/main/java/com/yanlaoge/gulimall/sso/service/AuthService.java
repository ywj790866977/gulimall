package com.yanlaoge.gulimall.sso.service;


import com.yanlaoge.gulimall.sso.util.AuthToken;

/**
 * @author rubyle
 * @date 2020/07/23
 */
public interface AuthService {

    /**
     * 授权认证方法
     *
     * @param username     用户名
     * @param password     密码
     * @param clientId     客户端id
     * @param clientSecret 客户端
     * @return token
     */
    AuthToken login(String username, String password, String clientId, String clientSecret);
}
