plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.serialization)
    id("localevents.android.library")
    id("localevents.hilt")
}

android {
    namespace = "com.gorman.database"
}

dependencies {
    implementation(project(":core:domain-model"))
    ksp(libs.room.compiler)
    implementation(libs.bundles.room)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.core.ktx)
}
