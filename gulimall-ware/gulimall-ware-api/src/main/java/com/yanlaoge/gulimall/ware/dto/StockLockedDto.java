package com.yanlaoge.gulimall.ware.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author ruyble
 * @date 2020/07/03
 */
@Data
@Builder
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

