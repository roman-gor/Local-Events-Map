plugins {
    alias(libs.plugins.kotlin.android)
    id("localevents.android.library")
    id("localevents.android.compose")
    id("localevents.hilt")
}

android {
    namespace = "com.gorman.feature_events"
}

dependencies {

    api(project(":core:common"))
    api(project(":core:domain-model"))
    api(project(":core:data"))
    api(project(":core:ui"))

    api(libs.bundles.hilt)
    api(libs.androidx.core.ktx)
    api(libs.androidx.lifecycle.runtime.ktx)
    api(libs.accompanist.permissions)
    api(libs.androidx.compose.ui.tooling.preview)
    api(libs.kotlinx.coroutines.immutable)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}
