package com.chen.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import io.netty.util.concurrent.ThreadPerTaskExecutor;
import lombok.*;

/**
 * @author Frozen
 * @since 2023-03-22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Report implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.INPUT)
    private Long reportId;

    private Long whistleblowerId;

    private Long defendantId;

    private String cause;

    private Integer handled;

    private String result;

    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModify;

    @TableLogic
    private Integer deleted;


}
