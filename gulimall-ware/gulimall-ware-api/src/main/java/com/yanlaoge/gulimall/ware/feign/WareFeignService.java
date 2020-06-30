package com.yanlaoge.gulimall.ware.feign;

import com.yanlaoge.common.utils.ResponseVo;
import com.yanlaoge.gulimall.ware.vo.FareVo;
import com.yanlaoge.gulimall.ware.vo.LockStockResVo;
import com.yanlaoge.gulimall.ware.vo.SkuHasStockVo;
import com.yanlaoge.gulimall.ware.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author rubyle
 */
@FeignClient("gulimall-ware")
public interface WareFeignService {
    /**
     * 查询库存
     *
     * @param skuIds 商品ids
     * @return R
     */
    @PostMapping("/ware/waresku/hasStock")
    ResponseVo<List<SkuHasStockVo>> getSkuHasStock(@RequestBody List<Long> skuIds);

    /**
     * 查询运费
     *
     * @param addrId 地址id
     * @return r
     */
    @GetMapping("ware/wareinfo/fare")
    ResponseVo<FareVo> getFare(@RequestParam("addrId") Long addrId);

    /**
     * 锁定库存
     * @param vo vo
     * @return R
     */
    @PostMapping("ware/waresku/lock/order")
    ResponseVo<List<LockStockResVo>> orderLockStock(@RequestBody WareSkuLockVo vo);
}
