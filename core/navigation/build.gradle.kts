plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.serialization)
    id("localevents.android.library")
    id("localevents.android.library.compose")
    id("localevents.hilt")
}

android {
    namespace = "com.gorman.navigation"
}

dependencies {
    implementation(project(":feature:setup:api"))
    implementation(project(":feature:auth:api"))
    implementation(project(":feature:events:api"))
    implementation(project(":feature:details-event:api"))
    implementation(project(":feature:bookmarks:api"))
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}
