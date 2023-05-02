package com.chen.service;


import com.chen.common.ReturnType;

import java.util.List;

public interface BidService {
    ReturnType getBids(Integer page);
    ReturnType deleteBid(Integer type, Long bidId, List<Long> bidIds);
}
