package com.yanlaoge.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author 好人
 * @date 2020-05-22 10:18
 **/
@Data
public class MergeVo {
    private Long purchaseId;
    private List<Long> items;
}
