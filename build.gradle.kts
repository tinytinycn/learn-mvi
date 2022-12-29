import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
    google()
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                // https://mvnrepository.com/artifact/moe.tlaster/precompose
                implementation("moe.tlaster:precompose:1.3.13")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.4")
                // https://mvnrepository.com/artifact/moe.tlaster/precompose-molecule
                implementation("moe.tlaster:precompose-molecule:1.3.13")
                implementation("app.cash.molecule:molecule-runtime:0.6.1")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "learn-mvi"
            packageVersion = "1.0.0"
        }
    }
}
