package com.yanlaoge.gulimall.product.web;

import com.yanlaoge.gulimall.product.entity.CategoryEntity;
import com.yanlaoge.gulimall.product.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 好人
 * @date 2020-05-27 23:20
 **/
@Controller
public class IndexController {

    @Resource
    private CategoryService categoryService;

    @GetMapping({"/","/index.html"})
    private String index(Model model){
        // TODO 查询1级分类
        List<CategoryEntity> list =  categoryService.getLevel1Categorys();
        model.addAttribute("categorys",list);
        return "index";
    }
}
