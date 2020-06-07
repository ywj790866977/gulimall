package com.yanlaoge.gulimall.member.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @author rubyle
 * @date 2020-06-06
 */
@Data
public class MemberRegisterVo {
    private String userName;
    private String password;
    private String phone;

}
