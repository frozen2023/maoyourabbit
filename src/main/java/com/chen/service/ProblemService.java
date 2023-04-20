package com.chen.service;

import com.chen.common.ReturnType;
import com.chen.pojo.Problem;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Frozen
 * @since 2023-03-22
 */
public interface ProblemService {
    ReturnType getProblems(Integer page, Integer solved);
    ReturnType handleProblem(Long problemId, Double level);
}
