package com.yanlaoge.gulimall.product.controller;

import com.yanlaoge.gulimall.product.vo.AttrGroupRelationVo;
import com.yanlaoge.gulimall.product.vo.AttrResVo;
import com.yanlaoge.gulimall.product.vo.AttrVo;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yanlaoge.gulimall.product.service.AttrService;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.common.utils.R;


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

	@Autowired
	private AttrService attrService;

	/**
	 * /product/attr/base/list/0? 列表 /product/attr/sale/list/0? 列表
	 */
	@GetMapping("/{attrType}/list/{catelogId}")
	//@RequiresPermissions("product:attr:list")
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
	@RequestMapping("/info/{attrId}")
	//@RequiresPermissions("product:attr:info")
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
