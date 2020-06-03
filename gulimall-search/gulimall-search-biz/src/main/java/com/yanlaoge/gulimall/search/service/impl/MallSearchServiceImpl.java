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
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
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
        //2.准备请求
        SearchRequest searchRequest = builderSearchRequest(vo);
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
        queryBuilder.filter(QueryBuilders.termQuery("hasStock",vo.getHasStock() == 1));

        // 2.5 价格区间
        // 进行价格区间查询,无结果
//        if(!StringUtils.isEmpty(vo.getSkuPrice())){
//            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
//            String[] s = vo.getSkuPrice().split("_");
//            if(s.length == 2){
//                rangeQuery.gte(s[0]).lte(s[1]);
//            }else if(s.length == 1){
//                if("_".startsWith(vo.getSkuPrice())){
//                    rangeQuery.lte(s[0]);
//                }
//                if("_".endsWith(vo.getSkuPrice())){
//                    rangeQuery.gte(s[0]);
//                }
//            }
//            queryBuilder.filter(rangeQuery);
//        }

        return queryBuilder;
    }

    private SearchResponseVo builderSearchResponseVo() {
        return null;
    }

    private SearchRequest builderSearchRequest(SearchParamVo vo) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //查询条件
        sourceBuilder.query(builderQuery(vo));
        //排序条件
        buiderSort(sourceBuilder,vo);
        //分页条件
        buiderPage(sourceBuilder,vo);
        //高亮
        buiderHighlight(sourceBuilder,vo);
        //聚合
        builderAgg(sourceBuilder,vo);

        return new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX},sourceBuilder);
    }

    private void builderAgg(SearchSourceBuilder sourceBuilder, SearchParamVo vo) {
        // 1. 品牌聚合
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brand_agg");
        brandAgg.field("brandId").size(50);
        // 1.1 子聚合
        brandAgg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brandAgg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
        sourceBuilder.aggregation(brandAgg);

        //2. 分类聚合
        TermsAggregationBuilder catalogAgg = AggregationBuilders.terms("catalog_agg").field("catalogId").size(20);
        // 2.1 子聚合
        catalogAgg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));
        sourceBuilder.aggregation(catalogAgg);

        //3.属性聚合
        // 3.1 进行内属性聚合
        NestedAggregationBuilder nested = AggregationBuilders.nested("attr_agg", "attrs");
        // 3.2 读哪个属性聚合
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        // 3.2 对聚合的属性id,进行聚合他的name和value
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        nested.subAggregation(attrIdAgg);

        sourceBuilder.aggregation(nested);
    }

    private void buiderHighlight(SearchSourceBuilder sourceBuilder, SearchParamVo vo) {
        if(!StringUtils.isEmpty(vo.getKeyword())){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            sourceBuilder.highlighter(highlightBuilder);
        }
    }

    private void buiderPage(SearchSourceBuilder sourceBuilder, SearchParamVo vo) {
        sourceBuilder.from((vo.getPageNum() -1)*EsConstant.PAGESIZE);
        sourceBuilder.size(EsConstant.PAGESIZE);
    }

    private void buiderSort(SearchSourceBuilder sourceBuilder,SearchParamVo vo) {
        if(!StringUtils.isEmpty(vo.getSort())){
            String sort = vo.getSort();
            String[] s = sort.split("_");
            SortOrder sortOrder = EsConstant.ASC.equalsIgnoreCase(s[1]) ? SortOrder.ASC: SortOrder.DESC;
            sourceBuilder.sort(s[0],sortOrder);
        }
    }
}
