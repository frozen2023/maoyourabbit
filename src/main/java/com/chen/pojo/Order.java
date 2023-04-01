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
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "order_id",type = IdType.INPUT)
    private Long orderId;

    @TableField("buyer_price")
    private BigDecimal buyerPrice;

    @TableField("account_id")
    private Long accountId;

    @TableField("checked")
    private Integer checked;

    @TableField("paid")
    private Integer paid;

    @TableField("cancled")
    private Integer cancled;

    @TableField("fav")
    private Integer fav;

    @TableField("finished")
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
