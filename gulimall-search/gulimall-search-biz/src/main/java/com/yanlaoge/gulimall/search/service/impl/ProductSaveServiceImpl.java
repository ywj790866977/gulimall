package com.yanlaoge.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.yanlaoge.gulimall.search.config.EsConfig;
import com.yanlaoge.gulimall.search.constant.EsConstant;
import com.yanlaoge.gulimall.search.model.SkuModel;
import com.yanlaoge.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品save
 *
 * @author rubyle
 */
@Service
@Slf4j
public class ProductSaveServiceImpl implements ProductSaveService {

    @Resource
    private RestHighLevelClient client;

    @Override
    public Boolean productStatusUp(List<SkuModel> skuModelList) throws IOException {
        // 批量保存
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuModel skuModel : skuModelList) {
            // 1.
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(String.valueOf(skuModel.getSkuId()));
            String jsonString = JSON.toJSONString(skuModel);
            indexRequest.source(jsonString, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }

        BulkResponse bulk = client.bulk(bulkRequest, EsConfig.COMMON_OPTIONS);
        boolean b = bulk.hasFailures();
        // 是否有错误
        if(b){
            List<String> ids = Arrays.stream(bulk.getItems()).map(BulkItemResponse::getId).collect(Collectors.toList());
            log.error("【productStatusUp】 添加商品错误, ids: {}",ids);
        }
        // 是否成功
        return !b;
    }
}
