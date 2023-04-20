package com.chen.repository;

import com.chen.pojo.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order,Object> {
    Page<Order> findAllBySellerIdAndFinished(Long sellerId, Integer finished, Pageable pageable);
    Page<Order> findAllByBuyerIdAndFinished(Long buyerId, Integer finished, Pageable pageable);
}
