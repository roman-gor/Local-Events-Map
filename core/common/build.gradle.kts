plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.serialization)
    id("localevents.android.library")
    id("localevents.android.library.compose")
    id("localevents.hilt")
}

android {
    namespace = "com.gorman.common"
}

dependencies {

    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:domain-model"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.play.services.location)
    implementation(libs.yandex.android)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
}
