package com.yanlaoge.gulimall.product;

import com.yanlaoge.gulimall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author 好人
 * @date 2020-05-19 07:36
 **/
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestDemo {

    @Autowired
    private CategoryService categoryService;

    @Test
    public void test01(){
        List<Long> catelogIds = categoryService.findCatelogIds(225L);
        log.info("catelogIds : {}",catelogIds);
    }
}
