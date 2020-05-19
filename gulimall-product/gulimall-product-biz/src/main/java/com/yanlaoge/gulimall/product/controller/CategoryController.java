package com.yanlaoge.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.yanlaoge.gulimall.product.entity.CategoryEntity;
import com.yanlaoge.gulimall.product.service.CategoryService;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.common.utils.R;


/**
 * 商品三级分类
 *
 * @author rubyle
 * @date 2020-05-13 14:21:04
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	//@RequiresPermissions("product:category:list")
	public R list() {
		List<CategoryEntity> list = categoryService.list();
		List<CategoryEntity> categoryEntityList = categoryService.listByTree();
		return R.ok().put("data", list);
	}

	/**
	 * 列表
	 */
	@RequestMapping("/list/tree")
	//@RequiresPermissions("product:category:list")
	public R listByTree() {
		List<CategoryEntity> categoryEntityList = categoryService.listByTree();
		return R.ok().put("data", categoryEntityList);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{catId}")
	//@RequiresPermissions("product:category:info")
	public R info(@PathVariable("catId") Long catId) {
		CategoryEntity category = categoryService.getById(catId);

		return R.ok().put("category", category);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	//@RequiresPermissions("product:category:save")
	public R save(@RequestBody CategoryEntity category) {
		categoryService.save(category);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	//@RequiresPermissions("product:category:update")
	public R update(@RequestBody CategoryEntity category) {
		categoryService.updateById(category);

		return R.ok();
	}

	/**
	 * 批量更新
	 *
	 * @param categories 集合
	 * @return R
	 */
	@RequestMapping("/update/sort")
	//@RequiresPermissions("product:category:update")
	public R update(@RequestBody List<CategoryEntity> categories) {
		categoryService.updateBatchById(categories);
		return R.ok();
	}

	/**
	 * 删除
	 */
	@PostMapping("/delete")
	//@RequiresPermissions("product:category:delete")
	public R delete(@RequestBody Long[] catIds) {
//		categoryService.removeByIds(Arrays.asList(catIds));
		categoryService.removeMenuByIds(Arrays.asList(catIds));
		return R.ok();
	}

}
