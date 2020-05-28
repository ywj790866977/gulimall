package com.yanlaoge.gulimall.search;

import com.alibaba.fastjson.JSON;
import com.yanlaoge.gulimall.search.config.EsConfig;
import lombok.Data;
import lombok.ToString;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GulimallSearchApplicationTests {
    @Resource
    private RestHighLevelClient client;

    @Data
    @ToString
    static class Amount {

        private int account_number;
        private int balance;
        private String firstname;
        private String lastname;
        private int age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;

    }

    @Test
    public void searchData() throws IOException {
        // 创建查询请求
        SearchRequest searchRequest = new SearchRequest();
        //设置查询index
        searchRequest.indices("bank");
        // 创建查询语句
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();


        searchSourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));
        // 按照年龄聚合
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
        searchSourceBuilder.aggregation(ageAgg);
        // 平均薪资
        AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balance");
        searchSourceBuilder.aggregation(balanceAvg);

//        searchSourceBuilder.from();
//        searchSourceBuilder.size();
//        searchSourceBuilder.aggregation();

        //将查询语句放入查询请求
        searchRequest.source(searchSourceBuilder);
        //执行查询并获取res
        SearchResponse searchResponse = client.search(searchRequest, EsConfig.COMMON_OPTIONS);

//        System.out.println(searchResponse);
        // 结果
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            String sourceAsString = searchHit.getSourceAsString();
            Amount amount = JSON.parseObject(sourceAsString, Amount.class);
            System.out.println(amount);
        }

        // 聚合信息

        Aggregations aggregations = searchResponse.getAggregations();
        Terms ageAgg1 = aggregations.get("ageAgg");
        for (Terms.Bucket bucket : ageAgg1.getBuckets()) {
            String keyAsString = bucket.getKeyAsString();
            System.out.println("年龄 "+keyAsString);
        }

        Avg balanceAvg1 = aggregations.get("balanceAvg");
        double value = balanceAvg1.getValue();
        System.out.println("平均薪资:" + value);

    }

    @Test
    public void contextLoads() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");
//        indexRequest.source("userName","zhangsan","age",18);
        User user = new User();
        String s = JSON.toJSONString(user);
        indexRequest.source(user, XContentType.JSON);
        IndexResponse index = client.index(indexRequest, EsConfig.COMMON_OPTIONS);
    }

    @Data
    class User {
        private String userName;
        private String gender;
        private Integer age;
    }

}
