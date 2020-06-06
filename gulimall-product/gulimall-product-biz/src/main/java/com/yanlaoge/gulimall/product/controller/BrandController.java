package com.yanlaoge.gulimall.product.controller;

import com.yanlaoge.common.utils.ResponseHelper;
import com.yanlaoge.common.utils.ResponseVo;
import com.yanlaoge.common.valid.AddGroup;
import com.yanlaoge.common.valid.UpdateGroup;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.yanlaoge.gulimall.product.entity.BrandEntity;
import com.yanlaoge.gulimall.product.service.BrandService;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.common.utils.R;



/**
 * 品牌
 *
 * @author rubyle
 * @date 2020-05-13 14:21:04
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:brand:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }

    @GetMapping("/infos")
    public ResponseVo<List<BrandEntity>> infos(@RequestParam("brandIds") List<Long> ids){
        List<BrandEntity> brands  = brandService.getBrandsByIds(ids);
        return ResponseHelper.success(brands);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    //@RequiresPermissions("product:brand:info")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:brand:save")
    public R save(@RequestBody @Validated(AddGroup.class) BrandEntity brand){
		brandService.save(brand);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:brand:update")
    public R update(@RequestBody  @Validated(UpdateGroup.class) BrandEntity brand){
//		brandService.updateById(brand);
        brandService.updateDetail(brand);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("update/status")
    //@RequiresPermissions("product:brand:update")
    public R updateStatus(@RequestBody  @Validated(UpdateGroup.class) BrandEntity brand){
        brandService.updateById(brand);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:brand:delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
