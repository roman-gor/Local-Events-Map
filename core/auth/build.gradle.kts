plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.serialization)
    alias(libs.plugins.gms.google.services)
    id("localevents.android.library")
    id("localevents.android.library.compose")
    id("localevents.hilt")
}

android {
    namespace = "com.gorman.auth"
    defaultConfig {
        val webClientId: String by rootProject.extra
        buildConfigField("String", "WEB_CLIENT_ID", "\"$webClientId\"")
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:domain-model"))
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)
    implementation(libs.bundles.credentials)
    implementation(libs.androidx.core.ktx)
}
