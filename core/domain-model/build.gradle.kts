plugins {
    alias(libs.plugins.kotlin.android)
    id("localevents.android.library")
    id("localevents.hilt")
}

android {
    namespace = "com.gorman.domain_model"

}

dependencies {

    implementation(project(":core:database"))
    implementation(project(":core:network"))
    implementation(project(":core:auth"))
    implementation(project(":core:common"))

    ksp(libs.room.compiler)
    implementation(libs.bundles.room)
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    implementation(libs.yandex.android)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
}
