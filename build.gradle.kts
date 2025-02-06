buildscript {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://storage.googleapis.com/r8-releases/raw")
        }
    }

    dependencies {
        classpath(libs.gradle)
        classpath(libs.google.services)
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.hilt.android.gradle.plugin)
        classpath(libs.r8)
        classpath(libs.firebase.crashlytics.gradle)
        classpath(libs.room.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.protobuf) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.ksp) apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
    }
}

task<Delete>("clean") {
    delete(layout.buildDirectory.get())
}