package com.chen.common;

public interface OrderConstant {

    /*
    * 订单状态
    * */
    String AWAITING_PAY = "等待买家付款";
    String AWAITING_CHECK = "等待买家验货";
    String AWAITING_INSPECT = "等待卖家检查";
    String FINISH = "订单已完成";
    String CANCLE = "订单已取消";

    /*
    * 订单消息 : 0 买家显示
    *           1 卖家显示
    * */
    // 立即购买
    String[] BUYER_GET = {"你已拍下","买家已拍下"};
    String[] BUYER_PAY = {"你已付款","买家已付款"};
    String[] BUYER_CANCEL_ORDER = {"未付款,你取消了订单","买家取消了订单"};
    String[] BUYER_PAY_TIMEOUT = {"超时未支付，系统已取消了订单","买家超时未支付,系统已取消了订单"};
    // 账号消息
    String[] BUYER_CHECK = {"你已确认收货,交易完成","买家已确认收货,交易完成"};
    String[] BUYER_CHECK_REJECT = {"你已拒绝收货,等待卖家检查","买家已拒绝收货,请检查账号"};
    String[] BUYER_CHECK_TIMEOUT = {"超时未确认,系统已自动确认","买家超时未确认,系统已自动确认"};
    String[] BUYER_CANCEL_TRANSACTION = {"卖家已取消交易","你已取消交易"};
    String[] BUYER_CANCEL_TRANSACTION_REJECT = {"卖家拒绝取消交易,已提交至管理员处理","你已拒绝取消交易,已提交至管理员处理"};
    String[] BUYER_INSPECT_TIMEOUT = {"卖家超时未操作,系统已自动取消交易","超时未操作,系统已自动取消交易"};

}
