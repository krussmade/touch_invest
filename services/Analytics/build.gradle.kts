import com.google.protobuf.gradle.*
import io.ktor.plugin.features.*

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "1.9.10"
    id("io.ktor.plugin") version "2.3.4"

    // protobuf
    id("com.google.protobuf") version "0.9.4"
}

group = "com.weizen"
version = "0.0.1"

application {
    mainClass.set("com.weizen.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")

    // protobuf
    implementation("com.google.protobuf:protobuf-java:3.19.6")
    implementation("io.grpc:grpc-protobuf:1.58.0")

    api("com.google.protobuf:protobuf-kotlin:3.19.6")
    protobuf(fileTree("../../proto/"))

    // gRPC
    api("io.grpc:grpc-kotlin-stub:1.2.1")
    api("io.grpc:grpc-stub:1.44.0")
    runtimeOnly("io.grpc:grpc-netty:1.44.0")
}

protobuf {
    protoc {
        // The artifact spec for the Protobuf Compiler
        artifact = "com.google.protobuf:protoc:3.19.6"
    }
    plugins {
        // Optional: an artifact spec for a protoc plugin, with "grpc" as
        // the identifier, which can be referred to in the "plugins"
        // container of the "generateProtoTasks" closure.
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.58.0"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.2.1:jdk7@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
            it.builtins {
                id("kotlin")
            }
        }
    }
}

ktor {
    docker {
//        localImageName.set("analytics")
//        portMappings.set(
//            mutableListOf(
//                DockerPortMapping(
//                    50051, 50051
//                ),
//                DockerPortMapping(
//                    50052, 50052
//                )
//            )
//        )
        file("Dockerfile")
    }
}
