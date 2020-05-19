package com.yanlaoge.gulimall.product.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AttrVo
 *
 * @author js-rubyle
 * @date 2020/5/19 14:04
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AttrResVo extends AttrVo {

	/**
	 * 分类名称
	 */
	private String catelogName;
	/**
	 * 分组名称
	 */
	private String groupName;
	/**
	 *
	 */
	private List<Long> catelogPath;
}
