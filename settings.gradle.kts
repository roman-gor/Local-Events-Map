pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "LocalEventsMap"
include(":app")
include(":core")
include(":core:common")
include(":core:network")
include(":core:ui")
include(":core:database")
include(":core:domain-model")
include(":feature")
include(":feature:events")
include(":core:data")
include(":core:cache")
include(":sync")
include(":sync:work")
