package com.yanlaoge.gulimall.product;

import com.yanlaoge.gulimall.product.dao.AttrGroupDao;
import com.yanlaoge.gulimall.product.service.AttrGroupService;
import com.yanlaoge.gulimall.product.service.CategoryService;
import com.yanlaoge.gulimall.product.service.SkuSaleAttrValueService;
import com.yanlaoge.gulimall.product.vo.SkuItemSaleAttrVo;
import com.yanlaoge.gulimall.product.vo.SpuItemAttrGroupVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
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
    @Resource
    private AttrGroupDao attrGroupDao;
    @Resource
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Test
    public void test01(){
        List<Long> catelogIds = categoryService.findCatelogIds(225L);
        log.info("catelogIds : {}",catelogIds);
    }

    @Test
    public void test02(){
        List<SpuItemAttrGroupVo> attrGroupwithSpuId = attrGroupDao.getAttrGroupwithSpuId(12L, 225L);
        System.out.println(attrGroupwithSpuId);
    }

    @Test
    public void test03(){
        List<SkuItemSaleAttrVo> saleAttrsBySpuId = skuSaleAttrValueService.getSaleAttrsBySpuId(11L);
        System.out.println(saleAttrsBySpuId);
    }
}
