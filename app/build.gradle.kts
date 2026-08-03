plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

val hasFirebaseConfig = file("google-services.json").exists()

if (hasFirebaseConfig) {
    pluginManager.apply(libs.plugins.google.services.get().pluginId)
}

android {
    namespace = "com.portfolio.notelensai"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.portfolio.notelensai"
        minSdk = 23
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        vectorDrawables.useSupportLibrary = true

        buildConfigField(
            "boolean",
            "FIREBASE_CONFIGURED",
            hasFirebaseConfig.toString(),
        )
        buildConfigField(
            "String",
            "GEMINI_MODEL_NAME",
            "\"gemini-3.5-flash\"",
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    ksp(libs.hilt.compiler)

    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.mlkit.text.recognition)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.ai)
    debugImplementation(libs.firebase.appcheck.debug)
    releaseImplementation(libs.firebase.appcheck.playintegrity)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}