buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:2.1.0-1.0.29")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
    id("com.google.devtools.ksp") version "2.1.0-1.0.29" apply false
}