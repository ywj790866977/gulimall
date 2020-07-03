package com.yanlaoge.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.gulimall.ware.entity.PurchaseEntity;
import com.yanlaoge.gulimall.ware.vo.MergeVo;
import com.yanlaoge.gulimall.ware.vo.PurchaseFinishVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author rubyle
 * @date 2020-05-13 15:47:12
 */
public interface PurchaseService extends IService<PurchaseEntity> {
    /**
     * 分页查询
     *
     * @param params 分页参数
     * @return 分页
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * queryPageUnreceiveList
     *
     * @param params 分页参数
     * @return 分页
     */
    PageUtils queryPageUnreceiveList(Map<String, Object> params);

    /**
     * 合并订单
     *
     * @param mergeVo mervo
     */
    void merge(MergeVo mergeVo);

    /**
     * 领取采购单
     * @param ids 采购单ids
     */
    void received(List<Long> ids);

    /**
     * 完成
     * @param doneVo vo
     */
    void done(PurchaseFinishVo doneVo);
}

