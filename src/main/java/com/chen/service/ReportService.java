package com.chen.service;

import com.chen.common.ReturnType;
import com.chen.pojo.Report;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.pojo.User;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Frozen
 * @since 2023-03-22
 */
public interface ReportService {
    ReturnType addReport(MultipartFile[] images, Report report);
    ReturnType getReports(Integer page, Integer handled);
    ReturnType freeze(Long userId);
    ReturnType handle(Long reportId, Integer result);
    void toBlackList(User user);
}
