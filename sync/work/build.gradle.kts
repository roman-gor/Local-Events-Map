plugins {
    alias(libs.plugins.kotlin.android)
    id("localevents.android.library")
    id("localevents.android.library.compose")
    id("localevents.hilt")
}

android {
    namespace = "com.gorman.work"
}

dependencies {
    implementation(project(":core:data"))
    implementation(libs.bundles.hilt)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.androidx.work.ktx)
    implementation(libs.hilt.ext.work)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.hilt.common)
}
