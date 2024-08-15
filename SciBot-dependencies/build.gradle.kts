dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.json:json:20231013")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC")
}
plugins {
    kotlin("jvm") version "1.9.24"
}
repositories {
    gradlePluginPortal()
    mavenCentral()
    mavenLocal()
}
java.sourceCompatibility = JavaVersion.VERSION_21
