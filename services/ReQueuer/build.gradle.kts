import com.google.protobuf.gradle.*

plugins {
    kotlin("jvm") version "1.9.10"
    application

    // protobuf
    id("com.google.protobuf") version "0.9.4"

    // docker
    id("com.palantir.docker") version "0.22.1"
    id("com.palantir.docker-run") version "0.22.1"
    distribution
}

group = "com.mweizen"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

    // protobuf
    implementation("com.google.protobuf:protobuf-java:3.19.6")
    implementation("io.grpc:grpc-protobuf:1.58.0")

    api("com.google.protobuf:protobuf-kotlin:3.19.6")
    protobuf(fileTree("../../proto/"))

    // other in Ktor
    api("io.grpc:grpc-kotlin-stub:1.2.1")
    api("io.grpc:grpc-stub:1.44.0")
    runtimeOnly("io.grpc:grpc-netty:1.44.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "MainKt"
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

val imagePrefix = "cr.yandex/crpjukeldgu5s966oqqo/"

docker {
    name = "$imagePrefix${project.name.toLowerCase()}:${project.version}"
    files(tasks.jar.get().outputs)
    setDockerfile(file("Dockerfile"))
}

dockerRun {
    name = project.name.toLowerCase()
    image = "$imagePrefix${project.name.toLowerCase()}:${project.version}"
}
