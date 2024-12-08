plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp") version "1.9.22-1.0.16"
}

android {
    namespace = "com.capstone.fishguard"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.capstone.fishguard"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "BASE_URL", "\"https://newsapi.org/v2/\"")
        buildConfigField("String", "API_KEY", "\"801bd50c3d084a24b8961338e8b8e9db\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
        mlModelBinding = true
    }
}

dependencies {
    // AndroidX and Material
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)

    // TensorFlow Lite
    implementation(libs.tensorflow.lite)
    implementation(libs.tensorflow.lite.support.v044)
    implementation(libs.tensorflow.lite.metadata.v044)
    implementation(libs.tensorflow.lite.task.vision)

    // JSON Parsing
    implementation(libs.gson)

    // Image Loading Libraries
    implementation(libs.glide)
    implementation(libs.picasso)

    // Retrofit for Networking
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // OkHttp for Networking
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // Image Cropping
    implementation(libs.ucrop)
    implementation(libs.androidx.activity)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Lifecycle components
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Fragment and Activity KTX
    val fragment_version = "1.8.5"
    implementation("androidx.fragment:fragment:$fragment_version")
    implementation("androidx.fragment:fragment-ktx:$fragment_version")
    implementation("androidx.fragment:fragment-compose:$fragment_version")

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Testing Libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
