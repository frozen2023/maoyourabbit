package com.chen.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
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
public class Debt implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "debt_id",type = IdType.INPUT)
    private Long debtId;

    @TableField("debtor_id")
    private Long debtorId;

    @TableField("amount")
    private BigDecimal amount;

    @TableField("account_id")
    private Long accountId;

    @TableField("wiped")
    private Integer wiped;

    @TableField(value = "gmt_create",fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(value = "gmt_modify",fill = FieldFill.INSERT_UPDATE)
    private Date gmtModify;

    @TableField("deleted")
    @TableLogic
    private Integer deleted;


}
