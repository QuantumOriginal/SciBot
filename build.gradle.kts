plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") version "1.9.24"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.24")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.24")
    implementation("org.json:json:20210307")
    implementation("org.yaml:snakeyaml:1.29")
    implementation("org.http4k:http4k-core:4.9.9.0")
    implementation(files("lib/dependencies-0.1.jar"))
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.9.24")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

group = "ind.glowingstone"
version = "1.0-SNAPSHOT"
description = "SciBot"
java.sourceCompatibility = JavaVersion.VERSION_21

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "ind.glowingstone.MainKt"  // 替换为你的主类路径
    }
}
tasks.build {
    dependsOn("shadowJar")
}
