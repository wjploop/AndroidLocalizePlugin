package com.wjp

import java.security.MessageDigest

/**
 *
 * 信息摘要
 * 使用hash函数，为一个文件生成一个2^128次方的信息量，有128比特，16字节，转换成16进制的字符串，即32位
 * 有时候，只要16位，截取中间16位即可
 * @param
 * @param number 取多少位
 */
fun md5(source: String,number:Int=32): String {
    val digest = MessageDigest.getInstance("md5")
    digest.update(source.toByteArray())
    val bytes = digest.digest()
    //将摘要的字节数组转换成可见
    val sb = StringBuffer()
    for (byte in bytes) {
        var b: Int = when {
            byte < 0 -> (byte + 256)
            byte < 16 -> {
                sb.append("0")
                byte.toInt()
            }
            else -> byte.toInt()
        }
        sb.append(Integer.toHexString(b))
    }
    var result=sb.toString()
    if(number==16){ //若是不是默认的32位
       result=result.substring(8,24)
    }
    return result

}

