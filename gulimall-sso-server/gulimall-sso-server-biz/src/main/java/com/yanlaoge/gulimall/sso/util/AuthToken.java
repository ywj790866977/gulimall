package com.yanlaoge.gulimall.sso.util;

import lombok.Data;

import java.io.Serializable;

/**
 * @author rubyle
 * @date 2020/07/23
 */
@Data
public class AuthToken implements Serializable{

    /**
     * 令牌信息
     */
    private String accessToken;
    /**
     * 刷新token(refresh_token)
     */
    private String refreshToken;
    /**
     * jwt短令牌
     */
    private String jti;


}