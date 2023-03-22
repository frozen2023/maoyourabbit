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
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("game_name")
    private String gameName;

    @TableField("account_number")
    private String accountNumber;

    @TableField("account_password")
    private String accountPassword;

    @TableField("detail")
    private String detail;

    @TableField("price")
    private BigDecimal price;

    @TableField("seller_id")
    private Long sellerId;

    @TableId(value = "account_id",type = IdType.INPUT)
    private Long accountId;

    @TableField("varified")
    private Integer varified;

    @TableField("bought")
    private Integer bought;

    @TableField(value = "gmt_create",fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(value = "gmt_modify",fill = FieldFill.INSERT_UPDATE)
    private Date gmtModify;

    @TableField("deleted")
    @TableLogic
    private Integer deleted;


}
