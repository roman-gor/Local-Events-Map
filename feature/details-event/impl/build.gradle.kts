plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.serialization)
    id("localevents.android.library")
    id("localevents.android.library.compose")
    id("localevents.hilt")
}

android {
    namespace = "com.gorman.feature.details.impl"
}

dependencies {
    implementation(project(":feature"))
    implementation(project(":feature:details-event:api"))
    implementation(project(":core:navigation"))

    implementation(libs.bundles.coil)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.ui.graphics)
}
