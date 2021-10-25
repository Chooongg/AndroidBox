package com.chooongg.ext

import java.text.DecimalFormat

/**
 * @sample formatNumber("#.00")
 * @param pattern 格式
 *  0 = 数字
 *  # = 数字，零则不显示
 *  . = 小数分隔符
 *  - = 减号
 *  , = 分组分隔符
 *  E = 科学计数法
 *  % = 乘100显示百分比
 *  \u2030 = 乘1000显示千分比
 *  ' = 以上字符前缀，使其为字符显示
 */

fun Number.formatNumber(pattern: String): String = try {
    val decimalFormat = DecimalFormat(pattern)
    decimalFormat.format(this)
} catch (e: Exception) {
    toString()
}