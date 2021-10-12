plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
}

android {
    compileSdk = Versions.compileSdk

    defaultConfig {
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        named("debug") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        named("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    packagingOptions {
        resources {
            excludes += "META-INF/AL2.0"
            excludes += "META-INF/LGPL2.1"
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.0.3"
    }

    kapt {
        correctErrorTypes = true
    }
}

dependencies {

    implementation(Deps.lifecycle)
    implementation(Deps.material)

    // Compose
    implementation(Deps.composeUI)
    implementation(Deps.composeMaterial)
    implementation(Deps.composeTooling)
    implementation(Deps.composeNavigation)
    implementation(Deps.composeNavigationHilt)
    implementation(Deps.composeActivity)
    implementation(Deps.composeLiveData)

    // Hilt
    implementation(Deps.dagger)
    kapt(Deps.daggerCompiler)
    implementation(Deps.hilt)
    kapt(Deps.hiltCompiler)

    // Firebase
    implementation(platform(Deps.firebaseBom))
    implementation(Deps.firebaseAppCheck)
    implementation(Deps.firebaseAnalytics)
    implementation(Deps.firebaseAuth)
    implementation(Deps.firebaseFirestore)
    implementation(Deps.firebaseDatabase)
    implementation(Deps.gmsAuth)

    //Coroutines
    implementation(Deps.coroutinesCore)
    implementation(Deps.coroutinesAndroid)
    implementation(Deps.coroutinesPS)

    // Accompanist
    implementation(Deps.accompanistUIController)
    implementation(Deps.accompanistPager)
    implementation(Deps.accompanistPagerIndicator)
    implementation(Deps.accompanistPagerInsets)
    implementation(Deps.accompanistNavigationAnimation)

    // Image
    implementation(Deps.lottie)
    implementation(Deps.coil)
    implementation(Deps.splashScreen)

    // Debug
    debugImplementation(Deps.composeUITestManifest)

    // Test
    testImplementation(Deps.junit)

    //Android Test
    androidTestImplementation(Deps.jUnitExt)
    androidTestImplementation(Deps.composeUITest)
}