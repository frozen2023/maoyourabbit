package com.chen.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

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

    @TableField(value = "gmt_create",fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(value = "gmt_modify",fill = FieldFill.INSERT_UPDATE)
    private Date gmtModify;

    @TableField("deleted")
    @TableLogic
    private Integer deleted;


}
