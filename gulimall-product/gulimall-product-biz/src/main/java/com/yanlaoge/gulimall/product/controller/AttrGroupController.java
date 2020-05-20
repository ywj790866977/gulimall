package com.yanlaoge.gulimall.product.controller;

import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.common.utils.R;
import com.yanlaoge.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.yanlaoge.gulimall.product.entity.AttrEntity;
import com.yanlaoge.gulimall.product.entity.AttrGroupEntity;
import com.yanlaoge.gulimall.product.service.AttrAttrgroupRelationService;
import com.yanlaoge.gulimall.product.service.AttrGroupService;
import com.yanlaoge.gulimall.product.service.AttrService;
import com.yanlaoge.gulimall.product.service.CategoryService;
import com.yanlaoge.gulimall.product.vo.AttrGroupRelationVo;
import com.yanlaoge.gulimall.product.vo.AttrGroupWithAttrsVo;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 属性分组
 *
 * @author rubyle
 * @date 2020-05-13 14:21:04
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {

	@Autowired
	private AttrGroupService attrGroupService;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private AttrService attrService;
	@Resource
	private AttrAttrgroupRelationService relationService;

	/**
	 * 获取分类下所有分组&关联信息
	 *
	 * @param catelogId 分类id
	 * @return vo
	 */
	@GetMapping("/{catelogId}/withattr")
	public R getAttrRelation(@PathVariable("catelogId") Long catelogId) {
		//1. 查询三级分类所有分组信息
		//2. 查询每个分组所有属性信息
		List<AttrGroupWithAttrsVo> relationEntities = attrGroupService.getAttrGroupwithAttrs(catelogId);
		return R.ok().put("data", relationEntities);
	}

	/**
	 * 根据vo获取保存
	 *
	 * @param relationVos vos
	 * @return R
	 */
	@PostMapping("/attr/relation")
	public R saveRelation(@RequestBody List<AttrGroupRelationVo> relationVos) {
		relationService.saveRelation(relationVos);
		return R.ok();
	}


	/**
	 * 根据组id查询所有属性
	 *
	 * @param attrgroupId 组id
	 * @return R
	 */
	@GetMapping("/{attrgroupId}/attr/relation")
	public R relationList(@PathVariable("attrgroupId") Long attrgroupId) {
		List<AttrEntity> attrEntityList = attrService.getRelationAttr(attrgroupId);
		return R.ok().put("data", attrEntityList);
	}

	/**
	 * 根据组id查询所有为关联属性
	 *
	 * @param attrgroupId 组id
	 * @return R
	 */
	@GetMapping("/{attrgroupId}/noattr/relation")
	public R noRelationList(@RequestParam Map<String, Object> params, @PathVariable("attrgroupId") Long attrgroupId) {
		PageUtils page = attrService.getNoRelationAttr(params, attrgroupId);
		return R.ok().put("page", page);
	}

	/**
	 * 删除关联属性
	 *
	 * @param relationVos 删除的集合
	 * @return R
	 */
	@PostMapping("/attr/relation/delete")
	public R lists(@RequestBody List<AttrGroupRelationVo> relationVos) {
		attrService.deleteRelation(relationVos);
		return R.ok();
	}

	/**
	 * 列表
	 */
	@RequestMapping("/list/{catelogId}")
	//@RequiresPermissions("product:attrgroup:list")
	public R list(@RequestParam Map<String, Object> params, @PathVariable("catelogId") Long catelogId) {
//        PageUtils page = attrGroupService.queryPage(params);
		PageUtils page = attrGroupService.queryPage(params, catelogId);

		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{attrGroupId}")
	//@RequiresPermissions("product:attrgroup:info")
	public R info(@PathVariable("attrGroupId") Long attrGroupId) {
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
		List<Long> catelogPath = categoryService.findCatelogIds(attrGroup.getCatelogId());
		attrGroup.setCatelogPath(catelogPath);
		return R.ok().put("attrGroup", attrGroup);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	//@RequiresPermissions("product:attrgroup:save")
	public R save(@RequestBody AttrGroupEntity attrGroup) {
		attrGroupService.save(attrGroup);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	//@RequiresPermissions("product:attrgroup:update")
	public R update(@RequestBody AttrGroupEntity attrGroup) {
		attrGroupService.updateById(attrGroup);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	//@RequiresPermissions("product:attrgroup:delete")
	public R delete(@RequestBody Long[] attrGroupIds) {
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

		return R.ok();
	}

}
