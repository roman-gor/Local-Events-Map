plugins {
    alias(libs.plugins.kotlin.android)
    id("localevents.android.library")
    id("localevents.hilt")
}

android {
    namespace = "com.gorman.firebase"
}

dependencies {

    implementation(project(":core:domain-model"))
    implementation(libs.kotlinx.serialization.json)
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
}
