plugins {
    alias(libs.plugins.androidApplication)
}

//allprojects {
//    repositories {
//        maven ( url = "https://jitpack.io" )
//    }
//}

android {
    namespace = "com.astatin3.scoutingapp2025"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.astatin3.scoutingapp2025"
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
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
//    implementation(libs.support.annotations)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


    var camerax_version = "1.3.2"
    implementation("androidx.camera:camera-core:1.3.2")
    implementation("androidx.camera:camera-camera2:1.3.2")
    implementation("androidx.camera:camera-lifecycle:1.3.2")
    implementation("androidx.camera:camera-view:${camerax_version}")

    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    implementation("com.github.skydoves:powerspinner:1.2.7")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation("com.github.douglasjunior:AndroidBluetoothLibrary:0.4.0")
//    implementation("com.github.DeveloperPaul123:SimpleBluetoothLibrary:1.5.1")

}

