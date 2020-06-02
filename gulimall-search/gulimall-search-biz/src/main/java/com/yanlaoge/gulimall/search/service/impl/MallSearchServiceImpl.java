package com.yanlaoge.gulimall.search.service.impl;

import com.yanlaoge.common.utils.Query;
import com.yanlaoge.gulimall.search.config.EsConfig;
import com.yanlaoge.gulimall.search.constant.EsConstant;
import com.yanlaoge.gulimall.search.service.MallSearchService;
import com.yanlaoge.gulimall.search.vo.SearchParamVo;
import com.yanlaoge.gulimall.search.vo.SearchResponseVo;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * searchService
 *
 * @author rubyle
 */
@Service
public class MallSearchServiceImpl implements MallSearchService {
    @Resource
    private RestHighLevelClient client;

    @SneakyThrows
    @Override
    public SearchResponseVo search(SearchParamVo vo) {
        //1. 构建query
        QueryBuilder queryBuilder = builderQuery(vo);
        //2.准备请求
        SearchRequest searchRequest = builderSearchRequest(queryBuilder);
        //3. 执行请求

        //4. 处理请求
        SearchResponse response = client.search(searchRequest, EsConfig.COMMON_OPTIONS);

        //5. 封装成结果
        SearchResponseVo res = builderSearchResponseVo();
        return res;
    }

    private QueryBuilder builderQuery(SearchParamVo vo) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        // 1.基本查询
        if(!StringUtils.isEmpty(vo.getKeyword())){
            queryBuilder.must(QueryBuilders.matchQuery("skuTitle",vo.getKeyword()));
        }

        // 2.过滤
        // 2.1 分类id
        if(vo.getCatalog3Id() != null){
            queryBuilder.filter(QueryBuilders.termQuery("catalogId",vo.getCatalog3Id()));
        }
        // 2.2 品牌id
        if(!CollectionUtils.isEmpty(vo.getBrandId())){
            queryBuilder.filter(QueryBuilders.termsQuery("brandId",vo.getBrandId()));
        }
        // 2.3 属性
        if(!CollectionUtils.isEmpty(vo.getAttrs())){
            for (String attr : vo.getAttrs()) {
                BoolQueryBuilder nestedBoolQueryBuilder = QueryBuilders.boolQuery();
                // attrs=1_5寸:8寸
                String[] s = attr.split("_");
                String attrId = s[0];
                String[] attrValue = s[1].split(":");
                nestedBoolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                nestedBoolQueryBuilder.must(QueryBuilders.termsQuery("attrs.attrValue",attrValue));
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs", nestedBoolQueryBuilder, ScoreMode.None);
                queryBuilder.filter(nestedQueryBuilder);
            }

        }
        // 2.4 库存
        queryBuilder.filter(QueryBuilders.termsQuery("hasStock",vo.getHasStock() == 1));

        // 2.5 价格区间
        if(!StringUtils.isEmpty(vo.getSkuPrice())){
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            String[] s = vo.getSkuPrice().split("_");
            if(s.length == 2){
                rangeQuery.gte(s[0]).lte(s[1]);
            }else if(s.length == 1){
                if("_".startsWith(vo.getSkuPrice())){
                    rangeQuery.lte(s[0]);
                }
                if("_".endsWith(vo.getSkuPrice())){
                    rangeQuery.gte(s[0]);
                }
            }
            queryBuilder.filter(rangeQuery);
        }

        return queryBuilder;
    }

    private SearchResponseVo builderSearchResponseVo() {
        return null;
    }

    private SearchRequest builderSearchRequest(QueryBuilder queryBuilder) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(queryBuilder);
        return new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX},sourceBuilder);
    }
}
