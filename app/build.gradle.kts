import com.android.build.api.dsl.Packaging
import java.util.Properties

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}

// Project-level build.gradle
buildscript {
    repositories {
        google()
        mavenCentral()
        jcenter()
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

      // Inject environment variables into BuildConfig
//        buildConfigField("String", "ACCESS_KEY", "\"${properties["ACCESS_KEY"]}\"")
//        buildConfigField("String", "SECRET_KEY", "\"${properties["SECRET_KEY"]}\"")
//        buildConfigField("String", "BUCKET_NAME", "\"${properties["BUCKET_NAME"]}\"")

        buildConfigField("String", "ACCESS_KEY", "\"${localProperties.getProperty("ACCESS_KEY")}\"")
        buildConfigField("String", "SECRET_KEY", "\"${localProperties.getProperty("SECRET_KEY")}\"")
        buildConfigField("String", "BUCKET_NAME", "\"${localProperties.getProperty("BUCKET_NAME")}\"")
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

    buildFeatures {
        buildConfig = true // Enable BuildConfig feature
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
    implementation("io.github.cdimascio:dotenv-java:3.0.0")

}