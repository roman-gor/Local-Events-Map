plugins {
    alias(libs.plugins.kotlin.android)
    id("localevents.android.library")
    id("localevents.hilt")
}

android {
    namespace = "com.gorman.domain_model"

}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
}
