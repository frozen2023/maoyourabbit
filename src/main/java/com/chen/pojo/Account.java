package com.chen.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.jetbrains.annotations.NotNull;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long sellerId;

    @TableId(value = "account_id",type = IdType.INPUT)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long accountId;

    @TableField("varified")
    private Integer varified;

    @TableField("bought")
    private Integer bought;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "mgt_create",fill = FieldFill.INSERT)
    private Date mgtCreate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "mgt_modify",fill = FieldFill.INSERT_UPDATE)
    private Date mgtModify;

    @TableField("deleted")
    @TableLogic
    private Integer deleted;


}
