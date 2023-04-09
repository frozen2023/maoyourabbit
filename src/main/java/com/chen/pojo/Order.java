package com.chen.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
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
@TableName("myt_order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "order_id",type = IdType.INPUT)
    private Long orderId;

    @TableField("buyer_price")
    private BigDecimal buyerPrice;

    @TableField("buyer_id")
    private Long buyerId;

    @TableField("seller_id")
    private Long sellerId;

    @TableField("account_id")
    private Long accountId;

    @TableField("od_checked")
    private Integer checked;

    @TableField("od_paid")
    private Integer paid;

    @TableField("od_cancled")
    private Integer cancled;

    @TableField("od_fav")
    private Integer fav;

    @TableField("od_finished")
    private Integer finished;

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
