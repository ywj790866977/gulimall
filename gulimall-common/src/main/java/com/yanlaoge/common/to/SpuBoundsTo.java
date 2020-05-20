package com.yanlaoge.common.to;

import java.math.BigDecimal;
import lombok.Data;

/**
 * SpuBoundsTo
 *
 * @author js-rubyle
 * @date 2020/5/21 0:54
 */
@Data
public class SpuBoundsTo {
	private Long spuId;
	private BigDecimal buyBounds;
	private BigDecimal growBounds;
}
