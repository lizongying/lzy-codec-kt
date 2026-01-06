package io.github.lizongying

import io.github.lizongying.Lzy.decodeToBytes
import io.github.lizongying.Lzy.decodeToString
import io.github.lizongying.Lzy.encodeFromBytes
import io.github.lizongying.Lzy.encodeFromString
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets


class LzyTest {


    @Test
    fun encode() {
        val testStr = "Hello 世界！LZY编码测试😀" // 包含Emoji（大于0xFFFF的字符）
        println("原始字符串: $testStr")

//        val lzy =

        // 编码流程
        val lzyBytes = encodeFromString(testStr)
        println("LZY编码字节: ${lzyBytes.contentToString()}")

        // 解码流程
        val decodedStr = decodeToString(lzyBytes)
        println("解码后字符串: $decodedStr")

        // 验证字符串一致性
        if (testStr == decodedStr) {
            println("✅ 编码解码一致性验证通过")
        } else {
            println("❌ 编码解码一致性验证失败")
        }
        assertEquals(testStr, decodedStr);

        // 测试字节流编码解码
        val utf8Bytes = testStr.toByteArray(StandardCharsets.UTF_8)
        val lzyBytes2 = encodeFromBytes(utf8Bytes)
        val decodedUtf8Bytes = decodeToBytes(lzyBytes2)

        // 验证字节数组一致性
        val isBytesEqual = utf8Bytes.contentEquals(decodedUtf8Bytes)
        if (isBytesEqual) {
            println("✅ 字节流编码解码一致性验证通过")
        } else {
            println("❌ 字节流编码解码一致性验证失败")
        }
    }
}