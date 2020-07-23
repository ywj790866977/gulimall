package com.yanlaoge.gulimall.sso.service;


import com.yanlaoge.gulimall.sso.util.AuthToken;

/**
 * @author rubyle
 * @date 2020/07/23
 */
public interface LoginService {
    /**
     * 模拟用户的行为 发送请求 申请令牌 返回
     *
     * @param username     用户名
     * @param password     密码
     * @param clientId     客户端id
     * @param clientSecret 客户端
     * @param grandType    授权类型
     * @return token
     */
    AuthToken login(String username, String password, String clientId, String clientSecret, String grandType);
}
