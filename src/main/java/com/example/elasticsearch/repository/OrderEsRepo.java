package com.example.elasticsearch.repository;

import com.example.elasticsearch.repository.entity.Order;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderEsRepo extends ElasticsearchRepository<Order,String> {
}
