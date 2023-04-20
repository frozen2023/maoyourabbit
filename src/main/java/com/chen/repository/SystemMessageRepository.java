package com.chen.repository;

import com.chen.pojo.SystemMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SystemMessageRepository extends MongoRepository<SystemMessage,String> {
    Page<SystemMessage> findAllByReceiverId(Long receiveId, Pageable pageable);
}
