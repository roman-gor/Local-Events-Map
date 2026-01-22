plugins {
    alias(libs.plugins.kotlin.android)
    id("localevents.android.library")
    id("localevents.android.compose")
    id("localevents.hilt")
}

android {
    namespace = "com.gorman.ui"
}

dependencies {

    implementation(project(":core:domain-model"))
    implementation(project(":core:common"))

    implementation(libs.bundles.hilt)
    ksp(libs.hilt.compiler)

    implementation(libs.bundles.compose)
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
