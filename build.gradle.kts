plugins {
    kotlin("jvm") version "2.3.0"
    `java-library`
    `maven-publish`
    signing
}

group = "io.github.lizongying"
version = "0.1.0"

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

signing {
    val signingKey: String? = System.getenv("GPG_PRIVATE_KEY")
    val signingPassword: String? = System.getenv("GPG_PASSPHRASE")

    if (signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications["mavenJava"])
    }
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
                    url.set("https://github.com/lizongying/lzy-codec-kt")
                }
            }
        }
    }

    repositories {
        if (version.toString().endsWith("-SNAPSHOT")) {
            maven {
                name = "OSSRHSnapshot"
                url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                credentials {
                    username = System.getenv("MAVEN_USERNAME")
                    password = System.getenv("MAVEN_PASSWORD")
                }
            }
        } else {
            maven {
                name = "OSSRHRelease"
                url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = System.getenv("MAVEN_USERNAME")
                    password = System.getenv("MAVEN_PASSWORD")
                }
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}