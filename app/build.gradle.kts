import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.gms.google-services")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.android")
    id("kotlinx-serialization")
    id("com.google.protobuf")
    id("com.google.firebase.crashlytics")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("androidx.room")
    id("kotlin-kapt")
}

android {
    buildFeatures {
        buildConfig = true
        compose = true
        viewBinding = true
        dataBinding = true
    }

    defaultConfig {
        applicationId = "org.jeonfeel.moeuibit2"
        minSdk = 26
        targetSdk = 35
        buildToolsVersion = "35.0.0"
        compileSdk = 35
        versionCode = 27
        versionName = "2.2.10"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file("key.jks")
            storePassword = "454842as"
            keyAlias = "seungpil"
            keyPassword = "454842as"
        }
    }

    buildTypes {
        getByName("release") {
//            isMinifyEnabled = true
//            isShrinkResources = true
            isDebuggable = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
            freeCompilerArgs.add("-Xjvm-default=all")
        }
    }

    composeCompiler {
        enableStrongSkippingMode = true
        includeSourceInformation = true
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }

    namespace = "org.jeonfeel.moeuibit2"
}

kapt {
    correctErrorTypes = true
}

configurations.all {
    exclude(group = "com.google.android.gms", module = "play-services-safetynet")
}

dependencies {
    // 기본 UI 및 레이아웃
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation(libs.legacy.support.v4)
    implementation(libs.core.ktx)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)

    // 테스트 관련
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // 네트워크 및 HTTP 관련
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.retrofit2.kotlinx.serialization.converter)

    // Chart 및 데이터 시각화
    implementation(libs.mpandroidchart)

    // Room 데이터베이스
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Glide 이미지 로딩
    implementation(libs.landscapist.glide)

    // 광고
    implementation(libs.play.services.ads)

    // 뷰모델 및 라이프사이클 관련
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.extensions)
    ksp(libs.lifecycle.compiler)

    // 코루틴
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlin.stdlib.jdk7)

    // 의존성 주입 (Hilt)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.android.compiler)

    // Compose UI 관련
    implementation(libs.activity.compose)
    implementation(libs.androidx.material)
    implementation(libs.androidx.animation)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.compose.shimmer)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material3:material3-window-size-class:1.3.1")
    implementation (libs.androidx.constraintlayout.compose)
    compileOnly(libs.compose.compiler.gradle.plugin)

    // 기타 유틸리티
    implementation(libs.logger)
    implementation(libs.toolbar.compose)

    // accompanist
    implementation(libs.accompanist.webview)
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)

    // Local Broadcast Manager
    implementation(libs.androidx.localbroadcastmanager)

    // Ktor 클라이언트
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.websockets)

    // DataStore 및 Protobuf
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore)
    implementation(libs.protobuf.java)
    implementation(libs.protobuf.kotlin)

    //Play Store inApp update
    implementation(libs.app.update)
    implementation(libs.app.update.ktx)

    // 기타 라이브러리
    implementation(libs.balloon)
    implementation(libs.flexhybridapp.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.metadata.jvm)
    implementation(libs.balloon.compose)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.1"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}

repositories {
    mavenCentral()
}