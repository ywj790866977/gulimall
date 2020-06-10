package com.yanlaoge.gulimall.cart.to;

import lombok.Data;

/**
 * @author rubyle
 */
@Data
public class UserInfoTo {
    private Long userId;
    private String userkey;
    private Boolean tempUser = false;
}
