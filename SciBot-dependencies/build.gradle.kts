dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.json:json:20231013")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("com.google.code.gson:gson:2.11.0")
}
plugins {
    kotlin("jvm") version "2.0.0"
}
repositories {
    gradlePluginPortal()
    mavenCentral()
    mavenLocal()
}
