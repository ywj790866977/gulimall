package com.yanlaoge.gulimall.product.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AttrTypeEum
 *
 * @author js-rubyle
 * @date 2020/5/19 16:17
 */
@AllArgsConstructor
@Getter
public enum AttrTypeEnum {
	/**
	 * base
	 */
	BASE("base",1),
	/**
	 * sale
	 */
	SALE("sale",0);
	private String type;
	private Integer code;
}
