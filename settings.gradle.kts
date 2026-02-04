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
include(":core:auth")
include(":core:common")
include(":core:network")
include(":core:ui")
include(":core:database")
include(":core:domain-model")
include(":core:data")
include(":core:cache")
include(":core:navigation")
include(":sync:work")
include(":feature:events:api")
include(":feature:events:impl")
include(":feature:details-event:api")
include(":feature:details-event:impl")
include(":feature:auth:api")
include(":feature:auth:impl")
include(":feature:setup:api")
include(":feature:setup:impl")
include(":feature:bookmarks:api")
include(":feature:bookmarks:impl")
include(":core:map")
