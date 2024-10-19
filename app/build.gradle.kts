plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.ecom.agrisewa"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ecom.agrisewa"
        minSdk = 24
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.retrofit)
    implementation(libs.gson)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.okhttp)
    implementation(libs.glide)
    implementation(libs.pinview)
    implementation(libs.dexter)
    implementation(libs.circleimageview)
    implementation(libs.checkout)
}