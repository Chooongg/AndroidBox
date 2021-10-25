package com.chooongg.ext

import java.nio.charset.Charset

fun String?.getByteUTF8Length() = this?.toByteArray(Charsets.UTF_8)?.size ?: 0
fun String?.getByteGB2312Length() = this?.toByteArray(Charset.forName("GB2312"))?.size ?: 0

/**
 *  替换选中段的字符
 *  @param startIndex 开始位置
 *  @param endIndex 结束位置
 *  @param replaceSymbol 替换符号
 */
fun String.changeCharToStar(startIndex: Int, endIndex: Int, replaceSymbol: String = "*"): String {
    val sb = StringBuffer()
    forEachIndexed { index, c ->
        if (index in startIndex until endIndex) {
            sb.append(replaceSymbol)
        } else {
            sb.append(c)
        }
    }
    return sb.toString()
}