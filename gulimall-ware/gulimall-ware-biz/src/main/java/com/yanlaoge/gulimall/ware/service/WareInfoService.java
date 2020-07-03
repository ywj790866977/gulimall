package com.yanlaoge.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.gulimall.ware.entity.WareInfoEntity;
import com.yanlaoge.gulimall.ware.vo.FareVo;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 仓库信息
 *
 * @author rubyle
 * @date 2020-05-13 15:47:12
 */
public interface WareInfoService extends IService<WareInfoEntity> {
    /**
     * 分页查询
     *
     * @param params 分页参数
     * @return 分页
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取运费信息
     *
     * @param addrId 地址id
     * @return 运费
     */
    FareVo getFare(Long addrId);

}

