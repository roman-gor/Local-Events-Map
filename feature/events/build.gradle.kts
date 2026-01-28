plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.serialization)
    id("localevents.android.library")
    id("localevents.android.library.compose")
    id("localevents.hilt")
}

android {
    namespace = "com.gorman.events"
}

dependencies {
    implementation(project(":feature"))

    implementation(libs.yandex.android)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.runtime)
}
