plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.serialization)
    id("localevents.android.library")
    id("localevents.hilt")
}

android {
    namespace = "com.gorman.cache"
}

dependencies {

    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.bundles.datastore)
    implementation(libs.yandex.android)
    implementation(libs.androidx.core.ktx)
}
