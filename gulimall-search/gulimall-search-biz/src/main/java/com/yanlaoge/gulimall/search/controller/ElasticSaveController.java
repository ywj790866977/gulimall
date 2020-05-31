package com.yanlaoge.gulimall.search.controller;

import com.yanlaoge.common.utils.ResponseHelper;
import com.yanlaoge.common.utils.ResponseVo;
import com.yanlaoge.gulimall.search.enums.ProductStatusEnum;
import com.yanlaoge.gulimall.search.model.SpuModel;
import com.yanlaoge.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 保存相关接口
 *
 * @author rubyle
 */
@Slf4j
@RestController
@RequestMapping("/search/save")
public class ElasticSaveController {

    @Resource
    private ProductSaveService productSaveService;

    @PostMapping("/product")
    public ResponseVo<Void> productStatusUp(@RequestBody List<SpuModel> spuModelList) {
        try {
            Boolean aBoolean = productSaveService.productStatusUp(spuModelList);
            if (!aBoolean){
                return ResponseHelper.error(ProductStatusEnum.PRODUCT_UP_EXCEPTOPN.getCode(),
                        ProductStatusEnum.PRODUCT_UP_EXCEPTOPN.getMsg());
            }
            return ResponseHelper.success();
        } catch (Exception e) {
            log.error("【productStatusUp】 保存商品信息异常 ", e);
            return ResponseHelper.error(ProductStatusEnum.PRODUCT_UP_EXCEPTOPN.getCode(),
                    ProductStatusEnum.PRODUCT_UP_EXCEPTOPN.getMsg());
        }
    }
}
