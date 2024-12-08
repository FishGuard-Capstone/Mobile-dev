pluginManagement {
    repositories {
        google() {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()  // Repositori Maven Central
        gradlePluginPortal()  // Gradle Plugin Portal
        maven { url = uri("https://jitpack.io") }  // Repositori JitPack
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)  // Menyaring repositori projek
    repositories {
        google()  // Repositori Google
        mavenCentral()  // Repositori Maven Central
        gradlePluginPortal()  // Gradle Plugin Portal
        maven { url = uri("https://jitpack.io") }  // Repositori JitPack
    }
}

rootProject.name = "FishGuard"  // Nama root proyek
include(":app")  // Menyertakan modul :app
