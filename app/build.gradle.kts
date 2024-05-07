import com.android.build.gradle.internal.packaging.defaultExcludes

plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.collegeapp.chatbot"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.collegeapp.chatbot"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}
dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(files("E:\\AndroidStudioProjects\\MyApplication\\app\\internal_libs\\JADE-bin-4.6.0\\jade\\lib\\jade.jar"))
    //implementation(files("E:\\AndroidStudioProjects\\MyApplication\\app\\internal_libs\\jade4android\\lib\\JadeLeapAndroid.jar"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}