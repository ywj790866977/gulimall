package com.yanlaoge.gulimall.search.service;

import com.yanlaoge.gulimall.search.model.SpuModel;

import java.io.IOException;
import java.util.List;

/**
 * 商品服务
 *
 * @author rubyle
 */
public interface ProductSaveService {
    /**
     * 商品上架
     *
     * @param spuModelList 商品集合
     * @throws IOException 查询异常
     * @return 是否成功
     */
    Boolean productStatusUp(List<SpuModel> spuModelList) throws IOException;
}
