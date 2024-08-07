dependencies {
    implementation(project(":SciBot-dependencies"))
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("org.yaml:snakeyaml:2.0")
    implementation("org.http4k:http4k-client-okhttp:4.15.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC")
    implementation("org.json:json:20231013")
}
plugins {
    kotlin("jvm") version "1.9.24"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}
repositories {
    gradlePluginPortal()
    mavenCentral()
    mavenLocal()
}
tasks.jar {
    manifest {
        attributes["Main-Class"] = "ind.glowingstone.MainKt"
    }
}
tasks.build {
    dependsOn("shadowJar")
}