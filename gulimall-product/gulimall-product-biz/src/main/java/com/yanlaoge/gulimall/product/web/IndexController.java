package com.yanlaoge.gulimall.product.web;

import com.yanlaoge.gulimall.product.entity.CategoryEntity;
import com.yanlaoge.gulimall.product.service.CategoryService;
import com.yanlaoge.gulimall.product.vo.Catalog2Vo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author 好人
 * @date 2020-05-27 23:20
 **/
@Controller
public class IndexController {

    @Resource
    private CategoryService categoryService;

    @GetMapping({"/", "/index.html"})
    public String index(Model model) {
        // TODO 查询1级分类
        List<CategoryEntity> list = categoryService.getLevel1Categorys();
        model.addAttribute("categorys", list);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catalog.json")
    public  Map<String,List<Catalog2Vo>> getCatelogJson() {
        return categoryService.getCatalogJson();
    }
}
