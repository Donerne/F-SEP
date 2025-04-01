import com.android.build.api.dsl.Packaging
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.util.Properties

// Project-level build.gradle
buildscript {
    repositories {
        google()
        mavenCentral()
        maven (url = "https://jitpack.io")  // Add this line
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

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.f_sep"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Load local.properties file
        val localProperties = Properties().apply {
            val localPropertiesFile = rootProject.file("local.properties")
            if (localPropertiesFile.exists()) {
                load(localPropertiesFile.inputStream())
            }
        }

//        buildConfigField("String", "ACCESS_KEY", "\"${project.properties["AWS_ACCESS_KEY"]}\"")
//        buildConfigField("String", "SECRET_KEY", "\"${project.properties["AWS_SECRET_KEY"]}\"")
//        buildConfigField("String", "BUCKET_NAME", "\"${project.properties["AWS_BUCKET_NAME"]}\"")

        // Set BuildConfig fields
        buildConfigField(
            type = "String",
            name = "ACCESS_KEY",
            value = localProperties.getProperty("ACCESS_KEY") ?: "\"null\""
        )
        buildConfigField(
            type = "String",
            name = "SECRET_KEY",
            value = localProperties.getProperty("SECRET_KEY") ?: "\"null\""
        )
        buildConfigField(
            type = "String",
            name = "BUCKET_NAME",
            value = localProperties.getProperty("BUCKET_NAME") ?: "\"null\""
        )
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
    // OpenCSV (for parsing CSV)
    implementation ("com.opencsv:opencsv:5.8")
//    //    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
//    implementation ("com.github.User:Repo:Tag")
    implementation("com.androidplot:androidplot-core:1.5.10")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Add AWS S3 dependencies for sign in and sign up activities
    implementation("com.amazonaws:aws-android-sdk-s3:2.72.0")
    implementation("com.amazonaws:aws-android-sdk-core:2.72.0")
    implementation("com.amazonaws:aws-android-sdk-auth-core:2.72.0")
}



//configurations.all {
//    resolutionStrategy {
//        force ("com.amazonaws:aws-java-sdk-core:1.12.250")  // Force the Java SDK version of core
//        force ("com.amazonaws:aws-java-sdk-s3:1.12.250")    // Force the Java SDK version of S3
//    }


