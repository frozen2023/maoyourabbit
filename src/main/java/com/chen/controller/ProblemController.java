package com.chen.controller;

import com.chen.common.ReturnType;
import com.chen.mapper.ProblemMapper;
import com.chen.security.annotations.IsAdmin;
import com.chen.service.ProblemService;
import com.chen.util.ObjectUtils;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Frozen
 * @since 2023-03-22
 */
@RestController
public class ProblemController {

    @Resource
    private ProblemService problemService;

    // 获取订单问题
    @IsAdmin
    @GetMapping("/problem/{page}/{solved}")
    public ReturnType getProblems(@PathVariable("page") Integer page, @PathVariable("solved") Integer solved) {
        return problemService.getProblems(page,solved);
    }

    // 处理订单问题
    @IsAdmin
    @PostMapping("/problem/handle")
    public ReturnType handleProblem(@RequestBody Map map) {
        Double level = ObjectUtils.toDouble(map.get("level"));
        Long problemId = ObjectUtils.toLong(map.get("problemId"));
        return problemService.handleProblem(problemId,level);
    }

}
