package com.yanlaoge.gulimall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 好人
 * @date 2020-06-30 17:22
 **/
@Data
public class FareVo {
    private MemberAddressVo address;
    private BigDecimal fare;
}
