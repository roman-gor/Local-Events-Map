plugins {
    alias(libs.plugins.kotlin.android)
    id("localevents.android.library")
    id("localevents.android.library.compose")
    id("localevents.hilt")
}

android {
    namespace = "com.gorman.ui"
}

dependencies {

    implementation(libs.bundles.hilt)
    ksp(libs.hilt.compiler)

    implementation(libs.bundles.compose)
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}
