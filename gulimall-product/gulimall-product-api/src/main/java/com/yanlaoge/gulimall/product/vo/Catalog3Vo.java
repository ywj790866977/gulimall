package com.yanlaoge.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * catelog2Vo
 *
 * @author  rubyle
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Catalog3Vo {
    private String id;
    private String name;
    private String catalog2Id;
}
