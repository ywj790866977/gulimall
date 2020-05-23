package com.yanlaoge.gulimall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author 好人
 * @date 2020-05-22 12:04
 **/
@Data
public class PurchaseFinishVo {

    /**
     * 采购单id
     */
    @NotNull
    private Long id;

    private List<PurchaseDoneItemVo> items;
}
