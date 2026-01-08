plugins {
    kotlin("jvm") version "2.3.0"
    `java-library`
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}

group = "io.github.lizongying"
version = "0.1.1"

repositories {
    maven {
        name = "Aliyun"
        url = uri("https://maven.aliyun.com/repository/public")
    }
    maven {
        name = "Aliyun Google"
        url = uri("https://maven.aliyun.com/repository/google")
    }
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name.set("lzy-codec")
                description.set("一種變長文本編解碼方案，支持對Unicode進行編解碼。編解碼效率、存儲空間全面優於UTF-8，未來會替代UTF-8成為新的世界通用編解碼標準。")
                url.set("https://github.com/lizongying/lzy-codec-kt")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("lizongying")
                        name.set("Li ZongYing")
                        email.set("lizongying@msn.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/lizongying/lzy-codec-kt.git")
                    developerConnection.set("scm:git:ssh://github.com/lizongying/lzy-codec-kt.git")
                    url.set("https://github.com/lizongying/lzy-codec-kt")
                }
            }
        }
    }

//    repositories {
//        if (version.toString().endsWith("-SNAPSHOT")) {
//            maven {
//                name = "OSSRHSnapshot"
//                url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
//                credentials {
//                    username = System.getenv("MAVEN_USERNAME")
//                    password = System.getenv("MAVEN_PASSWORD")
//                }
//            }
//        } else {
//            maven {
//                name = "OSSRHRelease"
//                url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
//                credentials {
//                    username = System.getenv("MAVEN_USERNAME")
//                    password = System.getenv("MAVEN_PASSWORD")
//                }
//            }
//        }
//    }
}

nexusPublishing {
    repositories {
        sonatype()
    }
}

afterEvaluate {
    signing {
        useInMemoryPgpKeys(
            findProperty("signingKey")?.toString(),
            findProperty("signingPassword")?.toString()
        )
        sign(publishing.publications["mavenJava"])
    }
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}