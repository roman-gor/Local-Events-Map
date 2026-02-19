import io.gitlab.arturbosch.detekt.Detekt
import java.util.Properties

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.google.devtools.ksp) apply false
    alias(libs.plugins.gms.google.services) apply false
    alias(libs.plugins.detekt)
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
    if (!localPropertiesFile.exists()) {
        error("Local.propeties file was not found. Create it and add the YANDEX_KEY API key")
    }
    properties.load(localPropertiesFile.inputStream())
    val key = properties.getProperty("YANDEX_KEY")
    if (key.isNullOrEmpty()) {
        error("YANDEX_KEY was not found in local.properties. Check \${layout.projectDirectory.file(\"README.md\")}")
    }
    return key
}

fun getWebClientId(): String {
    val properties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (!localPropertiesFile.exists()) {
        error("Local.propeties file was not found. Create it and add the WEB_CLIEN_ID")
    }
    properties.load(localPropertiesFile.inputStream())
    val key = properties.getProperty("WEB_CLIENT_ID")
    if (key.isNullOrEmpty()) {
        error("WEB_CLIENT_ID was not found in local.properties. Check \${layout.projectDirectory.file(\"README.md\")}")
    }
    return key
}

extra["yandexApiKey"] = getYandexApiKey()
extra["webClientId"] = getWebClientId()
