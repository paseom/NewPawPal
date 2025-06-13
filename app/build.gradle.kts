plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.newpawpal"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.newpawpal"
        minSdk = 29
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
}

dependencies {

    implementation (platform("com.google.firebase:firebase-bom:32.8.1"))
    implementation ("com.google.firebase:firebase-database")
    implementation ("com.google.firebase:firebase-auth")
    implementation ("com.google.android.gms:play-services-auth:21.0.0")
    implementation ("androidx.core:core-ktx:1.13.1")
    implementation ("androidx.recyclerview:recyclerview:1.3.2")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}