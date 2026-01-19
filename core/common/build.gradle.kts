plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.serialization)
    id("localevents.android.library")
    id("localevents.hilt")
}

android {
    namespace = "com.gorman.common"
}

dependencies {

    implementation(project(":core"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:domain-model"))
    implementation(libs.play.services.location)
    implementation(libs.yandex.android)
    implementation(libs.bundles.hilt)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
