plugins {
    alias(libs.plugins.kotlin.android)
    id("localevents.android.library")
}

android {
    namespace = "com.gorman.domain_model"

}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
