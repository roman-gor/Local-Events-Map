plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.serialization)
    id("localevents.android.library")
    id("localevents.android.library.compose")
    id("localevents.hilt")
}

android {
    namespace = "com.gorman.feature.setup.impl"
}

dependencies {
    implementation(project(":feature"))
    implementation(project(":feature:setup:api"))
    implementation(project(":feature:auth:api"))
    implementation(project(":feature:events:api"))
    implementation(project(":core:navigation"))
    implementation(project(":core:ui"))
    implementation(project(":core:cache"))

    implementation(libs.yandex.android)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.runtime)
}
