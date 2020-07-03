package com.yanlaoge.gulimall.product.web;

import com.yanlaoge.gulimall.product.entity.CategoryEntity;
import com.yanlaoge.gulimall.product.service.CategoryService;
import com.yanlaoge.gulimall.product.vo.Catalog2Vo;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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
    @Resource
    private RedissonClient redissonClient;

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

    @ResponseBody
    @GetMapping("hello")
    public String hello(){
        //获取锁
        RLock myLock = redissonClient.getLock("my-lock");
        // 加锁
        myLock.lock();
        try {
            System.out.printf("加锁成功,执行业务.."+Thread.currentThread().getId());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //解锁
            System.out.printf("解锁.."+Thread.currentThread().getId());
            myLock.unlock();
        }
        return "hello";
    }
}
