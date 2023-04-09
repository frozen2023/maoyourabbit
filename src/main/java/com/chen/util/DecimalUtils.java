package com.chen.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

// BigDecimal工具

public class DecimalUtils {

	/**
	 * 加法计算（result = x + y）
	 */
	public static BigDecimal add(BigDecimal x, BigDecimal y) {
		if (x == null) {
			return y;
		}
		if (y == null) {
			return x;
		}
		return x.add(y);
	}

	/**
	 * 加法计算（result = a + b + c + d）
	 */
	public static BigDecimal add(BigDecimal a, BigDecimal b, BigDecimal c, BigDecimal d) {
		BigDecimal ab = add(a, b);
		BigDecimal cd = add(c, d);
		return add(ab, cd);
	}

	/**
	 * 累加计算(result=x + result)
	 */
	public static BigDecimal accumulate(BigDecimal x, BigDecimal result) {
		if (x == null) {
			return result;
		}
		if (result == null) {
			result = new BigDecimal("0");
		}
		return result.add(x);
	}

	/**
	 * 减法计算(result = x - y)
	 */
	public static BigDecimal subtract(BigDecimal x, BigDecimal y) {
		if (x == null || y == null) {
			return null;
		}
		return x.subtract(y);
	}

	/**
	 * 乘法计算(result = x × y)
	 */
	public static BigDecimal multiply(BigDecimal x, BigDecimal y) {
		if (x == null || y == null) {
			return null;
		}
		return x.multiply(y);
	}

	/**
	 * 除法计算(result = x ÷ y)
	 */
	public static BigDecimal divide(BigDecimal x, BigDecimal y) {
		if (x == null || y == null || y.compareTo(BigDecimal.ZERO) == 0) {
			return null;
		}
		// 结果为0.000..时，不用科学计数法展示
		return stripTrailingZeros(x.divide(y, 20, BigDecimal.ROUND_HALF_UP));
	}

	/**
	 * 转为字符串(防止返回可续计数法表达式)
	 */
	public static String toPlainString(BigDecimal x) {
		if (x == null) {
			return null;
		}
		return x.toPlainString();
	}

	/**
	 * 保留小数位数
	 */
	public static BigDecimal scale(BigDecimal x, int scale) {
		if (x == null) {
			return null;
		}
		return x.setScale(scale, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 整型转为BigDecimal
	 */
	public static BigDecimal toBigDecimal(Integer x) {
		if (x == null) {
			return null;
		}
		return new BigDecimal(x.toString());
	}

	/**
	 * 长整型转为BigDecimal
	 */
	public static BigDecimal toBigDecimal(Long x) {
		if (x == null) {
			return null;
		}
		return new BigDecimal(x.toString());
	}

	/**
	 * 双精度型转为BigDecimal
	 */
	public static BigDecimal toBigDecimal(Double x) {
		if (x == null) {
			return null;
		}
		return new BigDecimal(x.toString());
	}

	/**
	 * 单精度型转为BigDecimal
	 */
	public static BigDecimal toBigDecimal(Float x) {
		if (x == null) {
			return null;
		}
		return new BigDecimal(x.toString());
	}

	/**
	 * 字符串型转为BigDecimal
	 */
	public static BigDecimal toBigDecimal(String x) {
		if (x == null || x.equals("")) {
			return null;
		}
		return new BigDecimal(x);
	}

	/**
	 * 对象类型转为BigDecimal
	 */
	public static BigDecimal toBigDecimal(Object x) {
		if (x == null) {
			return null;
		}
		BigDecimal result = null;
		try {
			result = toBigDecimal(x.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static BigDecimal toBigDecimal(Object x , int scale) {
		if (x == null) {
			return null;
		}
		BigDecimal result = null;
		try {
			result = toBigDecimal(x.toString());
			result.setScale(scale, RoundingMode.HALF_UP);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 倍数计算，用于单位换算
	 */
	public static BigDecimal multiple(BigDecimal x, Integer multiple) {
		if (x == null || multiple == null) {
			return null;
		}
		return DecimalUtils.multiply(x, toBigDecimal(multiple));
	}

	/**
	 * 去除小数点后的0（如: 输入1.000返回1）
	 */
	public static BigDecimal stripTrailingZeros(BigDecimal x) {
		if (x == null) {
			return null;
		}
		return x.stripTrailingZeros();
	}
}
