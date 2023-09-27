import com.google.protobuf.gradle.*

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "1.9.10"
    id("io.ktor.plugin") version "2.3.4"

    // protobuf
    id("com.google.protobuf") version "0.9.4"

    // docker
    id("com.palantir.docker") version "0.22.1"
    id("com.palantir.docker-run") version "0.22.1"
    distribution
}

group = "com.weizen"
version = "0.0.1"

application {
//    mainClass.set("com.weizen.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "com.weizen.ApplicationKt"
    }
    // To avoid the duplicate handling strategy error
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // To add all the dependencies
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
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
        file("Dockerfile")
    }
}

val imagePrefix = "cr.yandex/crpjukeldgu5s966oqqo/"

docker {
    name = "$imagePrefix${project.name.toLowerCase()}:${project.version}"
    files(tasks.jar.get().outputs)
    setDockerfile(file("Dockerfile"))
}

dockerRun {
    name = project.name.toLowerCase()
    image = "$imagePrefix${project.name.toLowerCase()}:${project.version}"
    ports(
        "50051:50051",
        "50052:50052"
    )
}
