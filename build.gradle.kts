plugins {
    kotlin("jvm") version "2.0.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.0.0")
    implementation("org.json:json:20231013")
    implementation("org.yaml:snakeyaml:2.0")
    implementation("org.http4k:http4k-core:4.9.9.0")
    implementation(files("lib/dependencies-0.1.jar"))
    implementation("org.http4k:http4k-client-okhttp:4.15.0.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.9.24")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

group = "ind.glowingstone"
version = "1.0-SNAPSHOT"
description = "SciBot"
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "ind.glowingstone.MainKt"
    }
}
tasks.build {
    dependsOn("shadowJar")
}
