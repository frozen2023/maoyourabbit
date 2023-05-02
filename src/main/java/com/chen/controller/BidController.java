package com.chen.controller;

import com.chen.common.ReturnType;
import com.chen.security.annotations.IsUser;
import com.chen.service.BidService;
import com.chen.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
public class BidController {
    @Resource
    private BidService bidService;

    @IsUser
    @GetMapping("/bid/{page}")
    public ReturnType getBids(@PathVariable("page") Integer page) {
        return bidService.getBids(page);
    }

    @IsUser
    @DeleteMapping("/bid")
    public ReturnType deleteBid(@RequestBody Map map) {
        Integer type = ObjectUtils.toInteger(map.get("type"));
        Long bidId = ObjectUtils.toLong(map.get("bidId"));
        List<Long> bidIds = ObjectUtils.toLongList(map.get("bidIds"));
        return bidService.deleteBid(type, bidId, bidIds);
    }
}
