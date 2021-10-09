// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.0.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.30")
    }
}

plugins {
    id("com.diffplug.spotless").version("5.7.0")
}

subprojects {
    repositories {
        google()
        mavenCentral()
    }

    apply(plugin = "com.diffplug.spotless")
    configure<com.diffplug.gradle.spotless.SpotlessExtension>  {
        kotlin {
            target("**/*.kt")
            targetExclude("**/src/test/resources/**")
            targetExclude("$buildDir/**/*.kt")
            targetExclude("bin/**/*.kt")

            trimTrailingWhitespace()
            indentWithSpaces()
            endWithNewline()

            ktlint("0.40.0")
        }
    }
}