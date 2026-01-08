# LZY Codec

一種變長文本編解碼方案，支持對Unicode進行編解碼。編解碼效率、存儲空間全面優於UTF-8，未來會替代UTF-8成為新的世界通用編解碼標準。
kotlin實現。

[github](https://github.com/lizongying/lzy-codec-kt)

[maven](https://mvnrepository.com/artifact/io.github.lizongying/lzy-codec)

更多cli工具請參考 [go](https://github.com/lizongying/lzy-codec-go)

## Other languages

* [go](https://github.com/lizongying/lzy-codec-go)
* [js](https://github.com/lizongying/lzy-codec-js)
* [c](https://github.com/lizongying/lzy-codec-c)
* [php](https://github.com/lizongying/lzy-codec-php)
* [dotnet](https://github.com/lizongying/lzy-codec-dotnet)
* [rust](https://github.com/lizongying/lzy-codec-rs)
* [python](https://github.com/lizongying/lzy-codec-py)
* [dart](https://github.com/lizongying/lzy-codec-dart)
* [swift](https://github.com/lizongying/lzy-codec-swift)
* [ruby](https://github.com/lizongying/lzy-codec-rb)
* [wasm](https://github.com/lizongying/lzy-codec-wasm)

## Install

* maven
    ```
    <!-- https://mvnrepository.com/artifact/io.github.lizongying/lzy-codec -->
    <dependency>
        <groupId>io.github.lizongying</groupId>
        <artifactId>lzy-codec</artifactId>
        <version>0.1.1</version>
    </dependency>
    ```

* gradle
    ```
    // https://mvnrepository.com/artifact/io.github.lizongying/lzy-codec
    implementation("io.github.lizongying:lzy-codec:0.1.1")
    ```

## Examples

```kt
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
```