package com.yanlaoge.gulimall.sso.util;


import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * @author rubyle
 * @date 2020/07/23
 */
public class UserJwt extends User {
    /**
     * 用户ID
     */
    private String id;
    /**
     * 用户名字
     */
    private String name;
    /**
     * 设置公司
     */
    private String comny;

    public UserJwt(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
