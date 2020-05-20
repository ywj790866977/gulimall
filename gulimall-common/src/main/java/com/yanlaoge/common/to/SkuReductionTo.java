package com.yanlaoge.common.to;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

/**
 * SpuReductionTo
 *
 * @author js-rubyle
 * @date 2020/5/21 1:03
 */
@Data
public class SkuReductionTo {
	private Long skuId;
	private Integer fullCount;
	private BigDecimal discount;
	private Integer countStatus;
	private BigDecimal fullPrice;
	private BigDecimal reducePrice;
	private Integer priceStatus;
	private List<MemberPrice> memberPrice;
}
