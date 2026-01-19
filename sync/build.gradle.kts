plugins {
    alias(libs.plugins.kotlin.android)
    id("localevents.android.library")
    id("localevents.android.compose")
    id("localevents.hilt")
}

android {
    namespace = "com.gorman.sync"
}

dependencies {
    api(libs.kotlinx.serialization.json)
    api(libs.kotlinx.coroutines)
    api(libs.kotlinx.coroutines.play.services)
    api(libs.bundles.hilt)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
