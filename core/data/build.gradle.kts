plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.serialization)
    id("localevents.android.library")
    id("localevents.hilt")
}

android {
    namespace = "com.gorman.data"
}

dependencies {

    implementation(project(":core:database"))
    implementation(project(":core:map"))
    implementation(project(":core:network"))
    implementation(project(":core:auth"))
    implementation(project(":core:common"))
    implementation(project(":core:cache"))
    implementation(project(":core:domain-model"))
    implementation(project(":core:notifications"))

    ksp(libs.room.compiler)
    implementation(libs.bundles.room)
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)
    implementation(libs.kotlin.datetime)

    implementation(libs.yandex.android)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.bundles.junit.tests)
}
