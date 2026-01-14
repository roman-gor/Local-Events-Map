import io.gitlab.arturbosch.detekt.Detekt
import java.util.Properties

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.serialization) apply false
    id("com.google.dagger.hilt.android") version "2.56.2" apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
    id("com.google.gms.google-services") version "4.4.3" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
    alias(libs.plugins.android.library) apply false
}

buildscript {
    dependencies {
        classpath(libs.hilt.android.gradle.plugin)
    }
}

val kotlinFiles = "**/*.kt"
val resourceFiles = "**/resources/**"
val buildFiles = "**/build/**"

/**
 * Task to run Detekt analysis for all project and included modules
 */
tasks.register("detektAll", Detekt::class) {
    val autoFix = project.hasProperty("detektAutoFix")

    description = "detekt build for all modules in this project"

    parallel = true
    ignoreFailures = false
    autoCorrect = autoFix
    buildUponDefaultConfig = true

    // To generate reports with relative paths
    basePath = projectDir.canonicalPath

    setSource(file(projectDir))
    config.setFrom("config/detekt/detekt.yml")
    include(kotlinFiles)
    exclude(resourceFiles, buildFiles)
    reports {
        html.required.set(true)
        xml.required.set(true)
        txt.required.set(false)
    }
}

dependencies {
    detektPlugins(libs.detekt.formatting.plugin)
}


fun getYandexApiKey(): String {
    val properties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        properties.load(localPropertiesFile.inputStream())
    }
    return properties.getProperty("YANDEX_KEY", "")
}

extra["yandexApiKey"] = getYandexApiKey()
