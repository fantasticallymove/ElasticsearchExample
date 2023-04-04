package com.example.elasticsearch.controller;

import com.example.elasticsearch.repository.OrderEsRepo;
import com.example.elasticsearch.repository.entity.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.example.elasticsearch.ElasticsearchApplication.JSON_PARSER;

@RequestMapping("/api")
@RestController
public class MainController {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ElasticsearchRestTemplate template;

    @Autowired
    private OrderEsRepo orderEsRepo;

    @GetMapping("/get/all")
    public ResponseEntity<String> getAllOrder() throws JsonProcessingException {
        return ResponseEntity.ok(JSON_PARSER.writeValueAsString(orderEsRepo.findAll()));
    }

    @PostMapping("/add")
    public ResponseEntity<String> add(@RequestBody Order order) {
        System.out.println(order);
        orderEsRepo.save(order);
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<String> getById(@PathVariable String id) throws IOException {
        BoolQueryBuilder must = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("loginName", "Jerdogfox"))
                .must(QueryBuilders.matchQuery("profitAmount", 0))
                .must(QueryBuilders.rangeQuery("betDate").gte("2023-09-10T00:00:00.000Z").lte("2023-09-30T00:00:00.000Z"));

        /**
         * 較新的請求方式 對於Entity需要定義 不定義的需要使用Map.class
         */
        Set<AbstractAggregationBuilder<?>> aggCondition = new HashSet<>();
        AggregationBuilders.min("oldestDate").field("betDate");
        aggCondition.add(AggregationBuilders.min("oldestDate").field("betDate"));
        aggCondition.add(AggregationBuilders.avg("betAmountAvg").field("BetAmount"));
        aggCondition.add(AggregationBuilders.sum("betAmount").field("BetAmount"));
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(must)
                .withSorts(SortBuilders.fieldSort("betDate").order(SortOrder.DESC))
                .withAggregations(aggCondition)
                .build();
        searchQuery.setMaxResults(1000);
        template.search(searchQuery, Map.class, IndexCoordinates.of("i-order"));

        /**
         * 舊的請求方式 無須定義封裝樣式比較自由
         */
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.sort("betDate", SortOrder.DESC);
        searchSourceBuilder.size(1000);
        searchSourceBuilder.aggregation(AggregationBuilders.min("oldestDate").field("betDate"))
                .aggregations()
                .addAggregator(AggregationBuilders.avg("betAmountAvg").field("BetAmount"))
                .addAggregator(AggregationBuilders.sum("betAmount").field("BetAmount"));
        searchSourceBuilder.query(must);
        SearchRequest request = new SearchRequest("i-order");
        request.source(searchSourceBuilder);
        restHighLevelClient.search(request, RequestOptions.DEFAULT);


        Optional<Order> byId = orderEsRepo.findById(id);
        if (byId.isPresent()) {
            return ResponseEntity.ok(JSON_PARSER.writeValueAsString(byId.get()));
        } else {
            return ResponseEntity.ok("No result");
        }
    }
}
