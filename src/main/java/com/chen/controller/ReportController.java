package com.chen.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.common.ReturnType;
import com.chen.pojo.Report;
import com.chen.security.annotations.IsAdmin;
import com.chen.security.annotations.IsUser;
import com.chen.service.ReportService;
import com.chen.util.ObjectUtils;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Frozen
 * @since 2023-03-22
 */
@RestController
public class ReportController {

    @Resource
    private ReportService reportService;

    // 用户举报
    @IsUser
    @PostMapping("/report")
    public ReturnType addReport(@RequestPart("file") MultipartFile[] images, Report report) {
        return reportService.addReport(images,report);
    }

    // 获取所有举报
    @IsAdmin
    @GetMapping("/report/{page}/{handled}")
    public ReturnType getReports(@PathVariable("page") Integer page, @PathVariable("handled") Integer handled) {
        return reportService.getReports(page,handled);
    }

    // 紧急冻结
    @IsAdmin
    @PutMapping("/report/frozen")
    public ReturnType freeze(@RequestBody Map map) {
        Long userId = ObjectUtils.toLong(map.get("userId"));
        return reportService.freeze(userId);
    }

    // 处理举报
    @IsAdmin
    @PostMapping("/report/handle")
    public ReturnType handle(@RequestBody Map map) {
        Long reportId = ObjectUtils.toLong(map.get("reportId"));
        Integer result = ObjectUtils.toInteger(map.get("result"));
        return reportService.handle(reportId,result);
    }
}
