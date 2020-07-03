package com.yanlaoge.gulimall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.yanlaoge.common.utils.ResponseHelper;
import com.yanlaoge.common.utils.ResponseVo;
import com.yanlaoge.gulimall.ware.vo.LockStockResVo;
import com.yanlaoge.gulimall.ware.vo.SkuHasStockVo;
import com.yanlaoge.gulimall.ware.vo.WareSkuLockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.yanlaoge.gulimall.ware.entity.WareSkuEntity;
import com.yanlaoge.gulimall.ware.service.WareSkuService;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.common.utils.R;


/**
 * 商品库存
 *
 * @author rubyle
 * @email besokuser@163.com
 * @date 2020-05-13 15:47:12
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 锁定库存
     * @param vo vo
     * @return R
     */
    @PostMapping("/lock/order")
    public ResponseVo<Boolean> orderLockStock(@RequestBody WareSkuLockVo vo) {
        if (!wareSkuService.orderLockStock(vo)) {
            return ResponseHelper.error();
        }
        return ResponseHelper.success();
    }

    /**
     * 查询库存信息
     * @param skuIds skuIds
     * @return 信息vo
     */
    @PostMapping("/hasStock")
    public ResponseVo<List<SkuHasStockVo>> getSkuHasStock(@RequestBody List<Long> skuIds) {
        List<SkuHasStockVo> list = wareSkuService.getSkuHasStock(skuIds);
        return ResponseHelper.success(list);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
