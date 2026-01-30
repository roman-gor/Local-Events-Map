plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.serialization)
    id("localevents.android.library")
    id("localevents.android.library.compose")
    id("localevents.hilt")
}

android {
    namespace = "com.gorman.feature.bookmarks.impl"
}

dependencies {
    implementation(project(":feature"))

    api(project(":feature:bookmarks:api"))
    implementation(project(":feature:details-event:api"))
    implementation(project(":feature:events:api"))
    implementation(project(":feature:auth:api"))
    implementation(project(":core:navigation"))

    implementation(libs.bundles.coil)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.runtime)
}
