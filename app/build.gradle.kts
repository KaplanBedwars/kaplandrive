plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.kaplandev.kaplandrivenew"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kaplandev.kaplandrivenew"
        minSdk = 29
        targetSdk = 34
        versionCode = 21
        versionName = "19.2"

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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.retrofit)
    
    implementation(libs.retrofit)

    implementation(libs.converterMoshi)
    implementation(libs.recyclerview)
    implementation(libs.cardview)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}