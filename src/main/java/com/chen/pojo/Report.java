package com.chen.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.chen.util.SnowFlakeUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.sun.org.apache.xerces.internal.dom.PSVIDocumentImpl;
import io.netty.util.concurrent.ThreadPerTaskExecutor;
import lombok.*;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Frozen
 * @since 2023-03-22
 */
@Data
@AllArgsConstructor
@Document("Report")
public class Report implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final Integer FIND_ACCOUNT = 1;
    public static final Integer OTHERS = 2;
    @Id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long reportId;  // 举报id

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long whistleblowerId; // 告发者id

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long defendantId; // 被告者id

    private String cause; // 文本原因

    private List<String> urls; // 图片url

    private Integer type; // 类型：找回账号/其他

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long orderId; // type =1 时填入

    private Integer handled; // 是否被处理

    private Integer result; // 处理结果

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date mgtCreate; // 创建时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date handlingTime; // 处理时间

    public Report() {
        this.reportId = SnowFlakeUtil.getSnowFlakeId();
        this.mgtCreate = new Date();
        this.handled = 0;
    }
}
