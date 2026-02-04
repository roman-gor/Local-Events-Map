plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("localevents.android.library")
    id("localevents.android.library.compose")
    id("localevents.hilt")
}

android {
    namespace = "com.gorman.map"
}

dependencies {
    implementation(project(":core:domain-model"))
    implementation(project(":core:common"))

    implementation(libs.yandex.android)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.runtime)
}
