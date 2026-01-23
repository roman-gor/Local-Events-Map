plugins {
    alias(libs.plugins.kotlin.android)
    id("localevents.android.library")
    id("localevents.hilt")
}

android {
    namespace = "com.gorman.firebase"
}

dependencies {

    implementation(project(":core"))
    implementation(project(":core:domain-model"))

    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
