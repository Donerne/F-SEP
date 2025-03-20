import com.android.build.api.dsl.Packaging

// Project-level build.gradle
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:8.1.0")
    }
}

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.f_sep"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.f_sep"
        minSdk = 21
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    packagingOptions {
        exclude ("META-INF/DEPENDENCIES")
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

    // Add AWS S3 dependencies for sign in and sign up activities
    implementation("com.amazonaws:aws-android-sdk-s3:2.72.0")
    implementation("com.amazonaws:aws-android-sdk-core:2.72.0")
    implementation("com.amazonaws:aws-android-sdk-auth-core:2.72.0")
}


//repositories {
//    google()
//    mavenCentral()



//    implementation ("com.amazonaws:aws-java-sdk-core:1.12.250") // Using the Java SDK if needed
//
//    // AWS SDK for S3
//    implementation ("com.amazonaws:aws-java-sdk-s3:1.12.250")



//configurations.all {
//    resolutionStrategy {
//        force ("com.amazonaws:aws-java-sdk-core:1.12.250")  // Force the Java SDK version of core
//        force ("com.amazonaws:aws-java-sdk-s3:1.12.250")    // Force the Java SDK version of S3
//    }


