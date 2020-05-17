package com.yanlaoge.common.valid;

import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang.ArrayUtils;

/**
 * ListValueConstraintValidator
 * 校验 ListValue
 *
 * @author js-rubyle
 * @date 2020/5/17 18:52
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue,Integer> {

	private Set<Integer> set = new HashSet<>(2);
	/**
	 * 初始化方法
	 * @param constraintAnnotation 参数
	 */
	@Override
	public void initialize(ListValue constraintAnnotation) {
		int[] vals = constraintAnnotation.vals();
		if(!ArrayUtils.isEmpty(vals)) {
			for (int val : vals) {
				set.add(val);
			}
		}
	}

	/**
	 * 校验
	 * @param integer 需要校验的值
	 * @param constraintValidatorContext 上下文
	 * @return
	 */
	@Override
	public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
		return set.contains(integer);
	}
}
