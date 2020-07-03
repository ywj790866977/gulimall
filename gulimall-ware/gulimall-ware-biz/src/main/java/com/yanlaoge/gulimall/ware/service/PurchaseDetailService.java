package com.yanlaoge.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.gulimall.ware.entity.PurchaseDetailEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author rubyle
 * @date 2020-05-13 15:47:12
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {
    /**
     * 分页查询
     *
     * @param params 分页参数
     * @return 分页
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查询采购项
     * @param id 采购id
     * @return list
     */
    List<PurchaseDetailEntity> listDetailByPurchaseId(Long id);
}

