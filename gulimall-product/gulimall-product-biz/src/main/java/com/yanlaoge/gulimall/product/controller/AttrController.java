package com.yanlaoge.gulimall.product.controller;

import com.google.common.collect.Lists;
import com.yanlaoge.common.to.SkuReductionTo;
import com.yanlaoge.common.utils.ResponseVo;
import com.yanlaoge.gulimall.coupon.feign.CouponFeignService;
import com.yanlaoge.gulimall.product.entity.ProductAttrValueEntity;
import com.yanlaoge.gulimall.product.service.ProductAttrValueService;
import com.yanlaoge.gulimall.product.vo.AttrGroupRelationVo;
import com.yanlaoge.gulimall.product.vo.AttrResVo;
import com.yanlaoge.gulimall.product.vo.AttrVo;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.yanlaoge.gulimall.search.feign.SearchFeignService;
import com.yanlaoge.gulimall.search.model.SkuModel;
import com.yanlaoge.gulimall.ware.feign.WareFeignService;
import com.yanlaoge.gulimall.ware.vo.SkuHasStockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.yanlaoge.gulimall.product.service.AttrService;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.common.utils.R;

import javax.annotation.Resource;


/**
 * 商品属性
 *
 * @author rubyle
 * @email sunlightcs@gmail.com
 * @date 2020-05-13 14:21:04
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Resource
    private SearchFeignService searchFeignService;
    @Resource
    private CouponFeignService couponFeignService;
    @Resource
    private WareFeignService wareFeignService;
    @Autowired
    private AttrService attrService;
    @Resource
    private ProductAttrValueService productAttrValueService;

    @PostMapping("/test")
    public void test(){
//        List<SkuModel> skuModelList = Lists.newArrayList();
//        ResponseVo<Void> res= searchFeignService.productStatusUp(skuModelList);
        List<Long> skuIds = Lists.newArrayList();
        ResponseVo<List<SkuHasStockVo>> res = wareFeignService.getSkuHasStock(skuIds);
        System.out.println(res);
    }


    /**
     * baseAttrList
     *
     * @param spuId spuid
     * @return R
     */
    @GetMapping("/base/listforspu/{spuId}")
    public R baseAttrList(@PathVariable("spuId") Long spuId) {
        List<ProductAttrValueEntity> list = productAttrValueService.baseAttrListforspu(spuId);

        return R.ok().put("data", list);
    }

    @PostMapping("/update/{spuId}")
    public R updateBySpuid(@PathVariable("spuId") Long spuId, @RequestBody List<ProductAttrValueEntity> entities) {
        productAttrValueService.updateBySpuid(spuId,entities);

        return R.ok();
    }

    /**
     * /product/attr/base/list/0? 列表 /product/attr/sale/list/0? 列表
     */
    @GetMapping("/{attrType}/list/{catelogId}")
    public R saleList(@RequestParam Map<String, Object> params,
                      @PathVariable("attrType") String type, @PathVariable("catelogId") Long catelogId) {

        PageUtils page = attrService.queryBaseAttrList(params, catelogId, type);

        return R.ok().put("page", page);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId) {
        AttrResVo attr = attrService.getAttrInfo(attrId);
        return R.ok().put("attr", attr);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attrVo) {
        attrService.saveAttr(attrVo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVo attr) {
//		attrService.updateById(attr);
        attrService.updateAttr(attr);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds) {
        attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
