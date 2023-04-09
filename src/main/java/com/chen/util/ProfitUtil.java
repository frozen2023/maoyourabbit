package com.chen.util;

import java.math.BigDecimal;

// 计算卖家最终能得到的利润
public class ProfitUtil {
    public static final Double ratio = 0.2003; //利润比率

    /*
    * 最终余额 = 当前余额 + 出价 * 比率
    * */
    public static BigDecimal getFinalProfit(BigDecimal balance, BigDecimal bid) {
        BigDecimal finalProfit = DecimalUtils.multiply(bid,BigDecimal.valueOf(ratio));
        return DecimalUtils.add(balance,finalProfit);
    }
}
