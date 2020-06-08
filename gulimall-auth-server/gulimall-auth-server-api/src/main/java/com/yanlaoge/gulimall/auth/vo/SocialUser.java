package com.yanlaoge.gulimall.auth.vo;

import lombok.Data;

/**
 * @author 好人
 * @date 2020-06-08 09:54
 **/
@Data
public class SocialUser {
    private String accessToken;
    private String remindIn;
    private Long expiresIn;
    private String uid;
    private String isRealName;
}
