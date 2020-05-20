package com.yanlaoge.gulimall.product.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.yanlaoge.gulimall.product.entity.AttrEntity;
import java.util.List;
import lombok.Data;

/**
 * AttrGroupWithAttrVo
 *
 * @author js-rubyle
 * @date 2020/5/20 14:39
 */
@Data
public class AttrGroupWithAttrsVo {
	/**
	 * 分组id
	 */
	private Long attrGroupId;
	/**
	 * 组名
	 */
	private String attrGroupName;
	/**
	 * 排序
	 */
	private Integer sort;
	/**
	 * 描述
	 */
	private String descript;
	/**
	 * 组图标
	 */
	private String icon;
	/**
	 * 所属分类id
	 */
	private Long catelogId;
	/**
	 *
	 */
	private List<AttrEntity> attrs;
}
