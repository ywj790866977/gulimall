package com.yanlaoge.gulimall.product.web;

import com.yanlaoge.gulimall.product.service.SkuInfoService;
import com.yanlaoge.gulimall.product.vo.SkuItemVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

/**
 * @author 好人
 * @date 2020-06-05 22:17
 **/
@Controller
public class ItemController {
    @Resource
    private SkuInfoService skuInfoService;

    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo =  skuInfoService.item(skuId);
        model.addAttribute("item",skuItemVo);
        return "item";
    }
}
