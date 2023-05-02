package com.chen.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.chen.util.SnowFlakeUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Frozen
 * @since 2023-03-22
 */
@Data
@AllArgsConstructor
@Document("Order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    public Order() {
        orderId = SnowFlakeUtil.getSnowFlakeId();
        checked = 0;
        paid = 0;
        canceled = 0;
        fav = 0;
        finished = 0;
        mgtCreate = new Date();
    }

    @Id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long orderId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long accountId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long buyerId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long sellerId;

    private BigDecimal bid;

    private Integer checked;

    private Integer paid;

    private Integer canceled;

    private Integer fav;

    private Integer finished;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date mgtCreate;

    private String status;

    private List<OrderEvent> orderEvents;

}
