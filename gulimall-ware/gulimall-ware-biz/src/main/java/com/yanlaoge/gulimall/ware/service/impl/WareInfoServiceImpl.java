package com.yanlaoge.gulimall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.yanlaoge.common.utils.R;
import com.yanlaoge.gulimall.member.entity.MemberReceiveAddressEntity;
import com.yanlaoge.gulimall.member.feign.MemberFeignService;
import com.yanlaoge.gulimall.ware.vo.FareVo;
import com.yanlaoge.gulimall.ware.vo.MemberAddressVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.common.utils.Query;

import com.yanlaoge.gulimall.ware.dao.WareInfoDao;
import com.yanlaoge.gulimall.ware.entity.WareInfoEntity;
import com.yanlaoge.gulimall.ware.service.WareInfoService;

import javax.annotation.Resource;


/**
 * @author rubyle
 */
@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Resource
    private MemberFeignService memberFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and(o->{
                o.eq("id",key)
                        .or().like("name",key)
                        .or().like("address",key)
                        .or().like("areacode",key);
            });
        }
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public FareVo getFare(Long addrId) {
        FareVo fareVo = new FareVo();
        R addrR = memberFeignService.info(addrId);
        MemberAddressVo receiveAddress = addrR.getData("memberReceiveAddress",
                new TypeReference<MemberAddressVo>(){});
        if(receiveAddress != null){
            fareVo.setAddress(receiveAddress);
            //截取手机号作为运费
            String phone = receiveAddress.getPhone();
            String fare = phone.substring(phone.length() - 1, phone.length());
            fareVo.setFare(new BigDecimal(fare));
        }
        return fareVo;
    }

}