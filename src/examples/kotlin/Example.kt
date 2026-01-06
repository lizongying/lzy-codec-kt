import io.github.lizongying.Lzy.decodeToString
import io.github.lizongying.Lzy.encodeFromString

fun main() {
    val testStr = "Hello 世界！LZY编码测试😀" // 包含Emoji（大于0xFFFF的字符）
    println("原始字符串: $testStr")

    // 编码流程
    val lzyBytes = encodeFromString(testStr)
    println("LZY编码字节: ${lzyBytes.contentToString()}")

    // 解码流程
    val decodedStr = decodeToString(lzyBytes)
    println("解码后字符串: $decodedStr")
}