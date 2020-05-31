package com.yanlaoge.gulimall.search.controller;

import com.yanlaoge.gulimall.search.service.MallSearchService;
import com.yanlaoge.gulimall.search.vo.SearchParamVo;
import com.yanlaoge.gulimall.search.vo.SearchResponseVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * 检索
 *
 * @author rubyle
 */
@Controller
public class SearchController {

    @Resource
    private MallSearchService mallSearchService;

    @GetMapping("list.html")
    public String listPage(@RequestBody SearchParamVo vo, Model model){
         List<SearchResponseVo> list =  mallSearchService.search(vo);
         model.addAttribute("result",list);
        return "list";
    }
}
