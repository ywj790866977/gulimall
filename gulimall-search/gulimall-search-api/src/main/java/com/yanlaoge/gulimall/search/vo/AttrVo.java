package com.yanlaoge.gulimall.search.vo;

import lombok.Data;

import java.util.List;

/**
 *
 * @author rubyle
 */
@Data
public class AttrVo {
    private Long attrId;
    private String attrName;

    private List<String> attrValue;
}
