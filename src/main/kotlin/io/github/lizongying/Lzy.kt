package io.github.lizongying;

import java.nio.charset.StandardCharsets;
import java.util.*;

object Lzy {
    // 定义常量，与之前版本保持一致
    private const
    val SURROGATE_MIN = 0xD800

    private const
    val SURROGATE_MAX = 0xDFFF

    private const
    val UNICODE_MAX = 0x10FFFF
    private val ERROR_UNICODE = IllegalArgumentException("invalid unicode")

    /**
     * 验证Unicode码点是否有效（排除代理区字符）
     *
     * @param r Unicode码点
     * @return 有效性标识
     */
    fun validUnicode(r: Int): Boolean {
        return (r in 0 until SURROGATE_MIN) || (r in SURROGATE_MAX + 1..UNICODE_MAX)
    }

    /**
     * 将Unicode码点列表（对应Go的[]rune）转换为LZY编码的字节数组
     *
     * @param inputRunes 整数列表，每个元素是有效的Unicode码点
     * @return LZY编码的ByteArray
     */
    fun encode(inputRunes: List<Int>): ByteArray {
        // 使用ByteBuffer辅助构建，或直接用ArrayList预存字节，最终转换为ByteArray
        val byteList = ArrayList<Byte>(inputRunes.size) // 预分配容量，与原逻辑一致

        for (r in inputRunes) {
            when {
                r < 0x80 -> {
                    // 单字节编码：0xxxxxxx
                    byteList.add((r and 0xFF).toByte())
                }

                r < 0x4000 -> {
                    // 双字节编码：高7位 + 0x80 | 低7位
                    byteList.add(((r shr 7) and 0xFF).toByte())
                    byteList.add(((0x80 or (r and 0x7F)) and 0xFF).toByte())
                }

                else -> {
                    // 三字节编码：高7位 + 0x80|中间7位 + 0x80|低7位
                    byteList.add(((r shr 14) and 0xFF).toByte())
                    byteList.add(((0x80 or ((r shr 7) and 0x7F)) and 0xFF).toByte())
                    byteList.add(((0x80 or (r and 0x7F)) and 0xFF).toByte())
                }
            }
        }

        // 转换为ByteArray
        val result = ByteArray(byteList.size)
        for (i in byteList.indices) {
            result[i] = byteList[i]
        }
        return result
    }

    /**
     * 将UTF-16字符串（Kotlin原生String）转换为LZY编码的字节数组
     *
     * @param inputStr Kotlin原生字符串
     * @return LZY编码的ByteArray
     */
    fun encodeFromString(inputStr: String): ByteArray {
        // 将字符串转换为Unicode码点列表（处理代理对，获取完整码点）
        val runes = mutableListOf<Int>()
        var i = 0
        while (i < inputStr.length) {
            val charCode = inputStr[i].code
            // 检测高代理字符，处理代理对
            if (charCode in SURROGATE_MIN..SURROGATE_MAX && i + 1 < inputStr.length) {
                val lowCharCode = inputStr[i + 1].code
                // 计算完整Unicode码点
                val fullRune = ((charCode - SURROGATE_MIN) shl 10) + (lowCharCode - 0xDC00) + 0x10000
                runes.add(fullRune)
                i++ // 跳过低代理
            } else {
                runes.add(charCode)
            }
            i++
        }
        return encode(runes)
    }

    /**
     * 将UTF-8编码的字节数组转换为LZY编码的字节数组
     *
     * @param inputBytes UTF-8编码的ByteArray
     * @return LZY编码的ByteArray
     */
    fun encodeFromBytes(inputBytes: ByteArray): ByteArray {
        // 先将UTF-8字节解码为Kotlin字符串
        val inputStr = String(inputBytes, StandardCharsets.UTF_8)
        return encodeFromString(inputStr)
    }

    /**
     * 将LZY编码的字节数组解码为Unicode码点列表
     *
     * @param inputBytes LZY编码的ByteArray
     * @return Unicode码点列表
     * @throws IllegalArgumentException 无效LZY编码或Unicode码点时抛出异常
     */
    fun decode(inputBytes: ByteArray): List<Int> {
        val length = inputBytes.size
        if (length == 0) {
            throw ERROR_UNICODE
        }

        // 寻找第一个最高位为0的字节（有效起始位置）
        var startIdx = -1
        for (i in inputBytes.indices) {
            if ((inputBytes[i].toInt() and 0x80) == 0) {
                startIdx = i
                break
            }
        }

        if (startIdx == -1) {
            throw ERROR_UNICODE
        }

        val validLen = length - startIdx
        if (validLen == 0) {
            throw ERROR_UNICODE
        }

        // 预分配输出容量
//        val preCap = Math.max(validLen / 4, 1)
        val output = mutableListOf<Int>()
//        output.ensureCapacity(preCap) // 预分配容量，优化性能

        var r = 0
        for (i in startIdx until length) {
            val b = inputBytes[i].toInt() and 0xFF // 转换为无符号整数
            if ((b shr 7) == 0) {
                // 遇到单字节标记，处理上一个累积的码点（非起始位置）
                if (i > startIdx) {
                    if (!validUnicode(r)) {
                        throw ERROR_UNICODE
                    }
                    output.add(r)
                }
                // 重置为当前单字节值
                r = b
            } else {
                // 累积码点：左移7位 + 低7位（排除0x80标记位）
                if (r > (UNICODE_MAX shr 7)) {
                    throw ERROR_UNICODE
                }
                r = (r shl 7) or (b and 0x7F)
            }
        }

        // 处理最后一个累积的码点
        if (!validUnicode(r)) {
            throw ERROR_UNICODE
        }
        output.add(r)

        return output
    }

    /**
     * 将LZY编码的字节数组解码为Kotlin原生字符串（UTF-16）
     *
     * @param inputBytes LZY编码的ByteArray
     * @return Kotlin原生字符串
     * @throws IllegalArgumentException 无效LZY编码或Unicode码点时抛出异常
     */
    fun decodeToString(inputBytes: ByteArray): String {
        val runes = decode(inputBytes)
        val stringBuilder = StringBuilder()

        for (r in runes) {
            if (r <= 0xFFFF) {
                // 普通字符，直接转换
                stringBuilder.append(Char(r))
            } else {
                // 大于0xFFFF的字符，转换为代理对
                val offset = r - 0x10000
                val highSurrogate = (SURROGATE_MIN + (offset shr 10)).toChar()
                val lowSurrogate = (0xDC00 + (offset and 0x3FF)).toChar()
                stringBuilder.append(highSurrogate).append(lowSurrogate)
            }
        }

        return stringBuilder.toString()
    }

    /**
     * 将LZY编码的字节数组解码为UTF-8编码的字节数组
     *
     * @param inputBytes LZY编码的ByteArray
     * @return UTF-8编码的ByteArray
     * @throws IllegalArgumentException 无效LZY编码或Unicode码点时抛出异常
     */
    fun decodeToBytes(inputBytes: ByteArray): ByteArray {
        val outputStr = decodeToString(inputBytes)
        // 将字符串编码为UTF-8字节数组
        return outputStr.toByteArray(StandardCharsets.UTF_8)
    }
}
