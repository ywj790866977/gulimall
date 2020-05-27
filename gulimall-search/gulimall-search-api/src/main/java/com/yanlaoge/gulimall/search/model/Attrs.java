package com.yanlaoge.gulimall.search.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * attrs
 *
 * @author rubyle
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Attrs {
    private Long attrId;
    private String attrName;
    private String attrValue;
}
