@file:Suppress("UnstableApiUsage")

// settings.gradle.kts
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    plugins {
        id("org.jetbrains.kotlin.android") version "1.9.0"
        id("com.android.application") version "8.8.2"
        id("com.android.library") version "8.8.2"
        id("com.google.gms.google-services") version "4.4.3"
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io") // For MPAndroidChart
    }
}
rootProject.name = "KITCHENKART"
include(":app")