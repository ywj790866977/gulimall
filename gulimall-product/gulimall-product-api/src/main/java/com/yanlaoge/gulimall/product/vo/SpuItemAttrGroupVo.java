package com.yanlaoge.gulimall.product.vo;

import lombok.Data;

import java.util.List;

/**
 * @author 好人
 * @date 2020-06-06 08:24
 **/
@Data
public class SpuItemAttrGroupVo {
    private String goupName;
    private List<SpuBaseAttrVo> attrs;
}
