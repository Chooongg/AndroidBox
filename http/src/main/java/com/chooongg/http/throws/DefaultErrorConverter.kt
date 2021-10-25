package com.chooongg.http.throws

class DefaultErrorConverter : HttpErrorConverter() {
    override fun convertRelease(type: HttpException.Type) = when (type) {
        HttpException.Type.CUSTOM -> "用户自定义错误"
        HttpException.Type.NETWORK -> "请检查网络连接"
        HttpException.Type.TIMEOUT -> "请求连接超时"
        HttpException.Type.PARSE -> "数据出现异常"
        HttpException.Type.SSL -> "验证失败"
        HttpException.Type.EMPTY -> "没有数据"
        HttpException.Type.HTTP302 -> "链接重定向"
        HttpException.Type.HTTP400,
        HttpException.Type.HTTP401, HttpException.Type.HTTP403,
        HttpException.Type.HTTP404, HttpException.Type.HTTP405,
        HttpException.Type.HTTP406, HttpException.Type.HTTP407,
        HttpException.Type.HTTP408, HttpException.Type.HTTP409,
        HttpException.Type.HTTP410, HttpException.Type.HTTP411,
        HttpException.Type.HTTP412, HttpException.Type.HTTP413,
        HttpException.Type.HTTP414, HttpException.Type.HTTP415,
        HttpException.Type.HTTP416, HttpException.Type.HTTP417 -> "请求出现错误"
        HttpException.Type.HTTP500, HttpException.Type.HTTP501,
        HttpException.Type.HTTP502, HttpException.Type.HTTP503,
        HttpException.Type.HTTP504, HttpException.Type.HTTP505 -> "服务器遇到错误"
        else -> "未知错误"
    }

    override fun convertDebug(type: HttpException.Type) = when (type) {
        HttpException.Type.CUSTOM -> "用户自定义错误"
        HttpException.Type.NETWORK -> "网络不可用"
        HttpException.Type.TIMEOUT -> "请求连接超时"
        HttpException.Type.PARSE -> "请求实体解析失败"
        HttpException.Type.SSL -> "SSL证书验证失败"
        HttpException.Type.EMPTY -> "Body为空"
        HttpException.Type.HTTP302 -> "302:服务器要求重定向"
        HttpException.Type.HTTP400 -> "400:服务器不理解请求的语法"
        HttpException.Type.HTTP401 -> "401:请求要求身份验证"
        HttpException.Type.HTTP403 -> "403:服务器拒绝请求"
        HttpException.Type.HTTP404 -> "404:服务器找不到请求的资源"
        HttpException.Type.HTTP405 -> "405:禁用请求中指定的方法"
        HttpException.Type.HTTP406 -> "406:无法使用请求的内容特性响应请求的资源"
        HttpException.Type.HTTP407 -> "407:请求要求使用代理授权身份"
        HttpException.Type.HTTP408 -> "408:服务器等候请求时发生超时"
        HttpException.Type.HTTP409 -> "409:服务器再完成请求时发生冲突"
        HttpException.Type.HTTP410 -> "410:请求的资源已删除"
        HttpException.Type.HTTP411 -> "411:服务器不接受不含有效内容长度标头字段的请求"
        HttpException.Type.HTTP412 -> "412:服务器未满足请求中设置的前提条件"
        HttpException.Type.HTTP413 -> "413:请求实体过大超出服务器的处理能力"
        HttpException.Type.HTTP414 -> "414:请求的URI过长"
        HttpException.Type.HTTP415 -> "415:请求格式不受支持"
        HttpException.Type.HTTP416 -> "416:请求范围不符合要求"
        HttpException.Type.HTTP417 -> "417:请求未满足期望值"
        HttpException.Type.HTTP500 -> "500:服务器遇到错误"
        HttpException.Type.HTTP501 -> "501:服务器不具备完成请求的功能"
        HttpException.Type.HTTP502 -> "502:服务器网关错误"
        HttpException.Type.HTTP503 -> "503:服务器暂时不可用"
        HttpException.Type.HTTP504 -> "504:服务器网关超时"
        HttpException.Type.HTTP505 -> "505:服务器不支持HTTP协议"
        else -> "未知错误"
    }
}