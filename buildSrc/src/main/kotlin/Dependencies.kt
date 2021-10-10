/**
 * To define plugins
 */
object BuildPlugins {
    const val android = "com.android.tools.build:gradle:${Versions.gradlePlugin}"
    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val gms = "com.google.gms:google-services:${Versions.gms}"
    const val hiltGradle = "com.google.dagger:hilt-android-gradle-plugin:${Versions.hilt}"
}

/**
 * To define dependencies
 */
object Deps {
    const val ktx = "androidx.core:core-ktx:${Versions.ktx}"
    const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
    const val material = "com.google.android.material:material:${Versions.material}"
    const val splashScreen = "androidx.core:core-splashscreen:${Versions.splashScreen}"

    const val dataStore = "androidx.datastore:datastore-preferences:${Versions.dataStore}"

    const val composeUI = "androidx.compose.ui:ui:${Versions.compose}"
    const val composeMaterial = "androidx.compose.material:material:$${Versions.compose}"
    const val composeTooling = "androidx.compose.ui:ui-tooling:${Versions.compose}"
    const val composeNavigation = "androidx.navigation:navigation-compose:${Versions.composeNavigation}"
    const val composeNavigationHilt = "androidx.hilt:hilt-navigation-compose:${Versions.composeHiltNavigation}"
    const val composeActivity = "androidx.activity:activity-compose:${Versions.activityCompose}"


    const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutine}"
    const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutine}"
    const val coroutinesPS = "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:${Versions.coroutine}"

    const val accompanistUIController = "com.google.accompanist:accompanist-systemuicontroller:${Versions.accompanist}"
    const val accompanistPager = "com.google.accompanist:accompanist-pager:${Versions.accompanist}"
    const val accompanistPagerIndicator = "com.google.accompanist:accompanist-pager-indicators:${Versions.accompanist}"
    const val accompanistPagerInsets = "com.google.accompanist:accompanist-insets:${Versions.accompanist}"
    const val accompanistNavigationAnimation = "com.google.accompanist:accompanist-navigation-animation:${Versions.accompanist}"

    const val lifecycle = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}"

    const val lottie = "com.airbnb.android:lottie-compose:${Versions.lottie}"
    const val coil = "io.coil-kt:coil-compose:${Versions.coil}"

    const val firebaseBom = "com.google.firebase:firebase-bom:${Versions.firebaseBom}"
    const val firebaseAnalytics = "com.google.firebase:firebase-analytics-ktx"
    const val firebaseAuth = "com.google.firebase:firebase-auth-ktx"
    const val gmsAuth = "com.google.android.gms:play-services-auth:${Versions.gmsAuth}"

    const val dagger = "com.google.dagger:dagger:${Versions.dagger}"
    const val hilt = "com.google.dagger:hilt-android:${Versions.hilt}"
    const val daggerCompiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"
    const val hiltCompiler = "com.google.dagger:hilt-compiler:${Versions.hilt}"
    // DEBUG


    // TEST
    const val junit = "junit:junit:${Versions.jUnit}"

    // Android Test
    const val jUnitExt = "androidx.test.ext:junit:${Versions.jUnitExt}"
    const val composeUITest = "androidx.compose.ui:ui-test-junit4:${Versions.compose}"
    const val composeUITestManifest = "androidx.compose.ui:ui-test-manifest:${Versions.compose}"
}