package com.yanlaoge.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.common.utils.Query;
import com.yanlaoge.gulimall.ware.dao.PurchaseDao;
import com.yanlaoge.gulimall.ware.entity.PurchaseDetailEntity;
import com.yanlaoge.gulimall.ware.entity.PurchaseEntity;
import com.yanlaoge.gulimall.ware.enums.PurchaseDetailStatusEnum;
import com.yanlaoge.gulimall.ware.enums.PurchaseStatusEnum;
import com.yanlaoge.gulimall.ware.service.PurchaseDetailService;
import com.yanlaoge.gulimall.ware.service.PurchaseService;
import com.yanlaoge.gulimall.ware.service.WareSkuService;
import com.yanlaoge.gulimall.ware.vo.MergeVo;
import com.yanlaoge.gulimall.ware.vo.PurchaseDoneItemVo;
import com.yanlaoge.gulimall.ware.vo.PurchaseFinishVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Resource
    private PurchaseDetailService purchaseDetailService;
    @Resource
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceiveList(Map<String, Object> params) {

        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status", 0).or().eq("status", 1)
        );

        return new PageUtils(page);

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void merge(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if (purchaseId == null) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setStatus(PurchaseStatusEnum.CREATED.getCode());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }
        //TODO 检查单子状态

        //合并订单
        List<Long> items = mergeVo.getItems();
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> collect = items.stream().map(item -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(item);
            purchaseDetailEntity.setPurchaseId(finalPurchaseId);
            purchaseDetailEntity.setStatus(PurchaseDetailStatusEnum.ASSIGNED.getCode());
            return purchaseDetailEntity;
        }).collect(Collectors.toList());

        purchaseDetailService.updateBatchById(collect);
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }

    @Override
    public void received(List<Long> ids) {
        // 1. 确认当前采购状态
        List<PurchaseEntity> collect = ids.stream().map(this::getById)
                .filter(item -> PurchaseStatusEnum.CREATED.getCode().equals(item.getStatus())
                        || PurchaseStatusEnum.ASSIGNED.getCode().equals(item.getStatus()))
                .peek(item -> {
                    item.setStatus(PurchaseStatusEnum.RECEIVE.getCode());
                    item.setUpdateTime(new Date());
                })
                .collect(Collectors.toList());
        // 2. 改变状态
        this.updateBatchById(collect);

        // 3. 改变采购项状态
        collect.forEach(item -> {
            List<PurchaseDetailEntity> detailEntities = purchaseDetailService.listDetailByPurchaseId(item.getId());
            detailEntities.forEach(detail->{
                detail.setStatus(PurchaseDetailStatusEnum.RECEIVE.getCode());
            });
            purchaseDetailService.updateBatchById(detailEntities);
        });

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void done(PurchaseFinishVo doneVo) {

        Long id = doneVo.getId();
        //1. 改变采购项状态
        boolean flag = true;
        List<PurchaseDetailEntity> updates = Lists.newArrayList();
        for (PurchaseDoneItemVo item : doneVo.getItems()) {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            if(PurchaseDetailStatusEnum.HASERROR.getCode().equals(item.getStatus())){
                flag = false;
                purchaseDetailEntity.setStatus(item.getStatus());
            }else{
                purchaseDetailEntity.setStatus(PurchaseDetailStatusEnum.FINISH.getCode());
                //成功,进行入库
                PurchaseDetailEntity detailEntity = purchaseDetailService.getById(item.getItemId());

                wareSkuService.addStock(detailEntity.getSkuId(),detailEntity.getWareId(),detailEntity.getSkuNum());
            }
            purchaseDetailEntity.setId(item.getItemId());
            updates.add(purchaseDetailEntity);
        }
        purchaseDetailService.updateBatchById(updates);
        //2. 改变采购单状态
        PurchaseEntity entity = new PurchaseEntity();
        entity.setId(id);
        entity.setStatus(flag? PurchaseStatusEnum.FINISH.getCode() : PurchaseStatusEnum.HASERROR.getCode());
        entity.setUpdateTime(new Date());
        this.updateById(entity);
        //3. 入库


    }

}