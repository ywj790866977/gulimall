package com.yanlaoge.gulimall.ware.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author ruyble
 * @date 2020/07/03
 */
@Data
@Accessors(chain = true)
public class StockLockedDto {
    /**
     * 工作单id
     */
    private Long id;
    /**
     * 锁定详情
     */
    private StockLockDetailDto detailTo;
}

