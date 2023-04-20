package com.chen.common;

public interface OrderStatus {

    /*
    * 订单状态
    * */
    String AWAITING_PAY = "等待买家付款";
    String AWAITING_CHECK = "等待买家验货";
    String AWAITING_INSPECT = "等待卖家检查";
    String AWAITING_DEAL = "等待管理员处理";
    String FINISH = "订单已完成";
    String CANCEL = "订单已取消";

}
