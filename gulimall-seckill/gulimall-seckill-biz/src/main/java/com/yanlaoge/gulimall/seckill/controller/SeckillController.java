package com.yanlaoge.gulimall.seckill.controller;

import com.yanlaoge.common.utils.ResponseHelper;
import com.yanlaoge.common.utils.ResponseVo;
import com.yanlaoge.gulimall.seckill.service.SeckillService;
import com.yanlaoge.gulimall.seckill.to.SeckillSkuRedisTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 好人
 * @date 2020-07-10 21:54
 **/
@Controller
public class SeckillController {

    @Resource
    private SeckillService seckillService;

    @GetMapping("/currentSeckillSkus")
    @ResponseBody
    public ResponseVo<List<SeckillSkuRedisTo>> getCurrentSeckillSkus() {
        return ResponseHelper.success(seckillService.getCurrentSeckillSkus());
    }

    @GetMapping("/sku/seckill/{skuId}")
    @ResponseBody
    public ResponseVo<SeckillSkuRedisTo> getSkuSeckillInfo(@PathVariable("skuId") Long skuId) {
        return ResponseHelper.success(seckillService.getSkuSeckillInfo(skuId));
    }

    @GetMapping("/kill")
    public String secKill(@RequestParam("killId") String killId, @RequestParam("key") String key,
                          @RequestParam("num") String num, Model model) {
        String orderSn = seckillService.kill(killId, key, Integer.parseInt(num));
        model.addAttribute("orderSn",orderSn);
        return "success";
    }
}
