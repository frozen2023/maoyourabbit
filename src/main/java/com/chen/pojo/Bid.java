package com.chen.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bid implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.INPUT)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long bidId; // 出价id

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long AccountId; // 账号id

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long BidderId; // 出价人id

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long sellerId; // 卖家id

    private BigDecimal amount; // 出价金额

    @TableField(fill = FieldFill.INSERT)
    private Date mgtCreate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date mgtModify;

    @TableLogic
    private Integer deleted;
}
