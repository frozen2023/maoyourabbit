package com.chen.repository;

import com.chen.pojo.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReportRepository extends MongoRepository<Report,Object> {
    Page<Report> findAllByHandled(Integer handled, Pageable pageable);
}
