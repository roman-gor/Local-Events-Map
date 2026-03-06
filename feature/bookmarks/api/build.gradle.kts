plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.serialization)
    id("localevents.android.library")
    id("localevents.android.library.compose")
}

android {
    namespace = "com.gorman.feature.bookmarks.api"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}
