plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.serialization)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.gms.google.services)
    alias(libs.plugins.detekt)
    id("localevents.android.application.compose")
    id("localevents.hilt")
    id("kotlin-parcelize")
}

android {
    namespace = "com.gorman.localeventsmap"

    compileSdk {
        version = release(36)
    }

    defaultConfig {
        val yandexApiKey: String by rootProject.extra
        applicationId = "com.gorman.localeventsmap"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "YANDEX_KEY", "\"$yandexApiKey\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(project(":feature:setup:impl"))
    implementation(project(":feature:events:impl"))
    implementation(project(":feature:bookmarks:impl"))
    implementation(project(":feature:details-event:impl"))
    implementation(project(":feature:auth:impl"))
    implementation(project(":sync:work"))
    implementation(project(":core:navigation"))
    implementation(project(":core:ui"))
    implementation(project(":core:map"))
    implementation(project(":core:cache"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.hilt.ext.work)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(libs.bundles.hilt)
    ksp(libs.hilt.compiler)

    lintChecks(libs.compose.lint.checks)

    implementation(libs.yandex.android)
}
