package com.chen.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

/**
 * @author Frozen
 * @since 2023-03-22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Problem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "problem_id",type = IdType.INPUT)
    private Long problemId;

    @TableField("order_id")
    private Long orderId;

    @TableField("detail")
    private String detail;

    @TableField("solved")
    private Integer solved;

    @TableField("agreed")
    private Integer agreed;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "gmt_create",fill = FieldFill.INSERT)
    private Date mgtCreate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "gmt_modify",fill = FieldFill.INSERT_UPDATE)
    private Date mgtModify;

    @TableField("deleted")
    @TableLogic
    private Integer deleted;


}
