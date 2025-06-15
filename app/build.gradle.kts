plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.noddy.statussaver"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.noddy.statussaver"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }



    buildTypes {
        buildFeatures {
            viewBinding = true
        }
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // For ZoomImage
    implementation("com.jsibbold:zoomage:1.3.1")

    // For ExoPlayer
    implementation("androidx.media3:media3-exoplayer:1.7.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.7.1")
    implementation("androidx.media3:media3-ui:1.7.1")


    implementation("androidx.documentfile:documentfile:1.0.1")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.15.1")

    // Refresh
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")

    // For Storage
    implementation("com.anggrayudi:storage:1.5.5")

    // ViewModel and LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Video Player
    implementation("androidx.media3:media3-exoplayer:1.2.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.2.1")
    implementation("androidx.media3:media3-ui:1.2.1")

    // Zoomable Image View
    implementation("com.jsibbold:zoomage:1.3.1")

    // Storage helper
    implementation("com.anggrayudi:storage:1.5.5")

    // Gson for favorites
    implementation("com.google.code.gson:gson:2.10.1")

    // Video Trimmer
    implementation ("com.github.a914-gowtham:Android-video-trimmer:1.7.0")

    // Manually declare missing dependencies
    implementation ("com.arthenica:mobile-ffmpeg-min:4.3.1.LTS")
    implementation ("com.crystal:crystalrangeseekbar:1.1.3")

    // Charts for analytics
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Biometric authentication
    implementation("androidx.biometric:biometric:1.1.0")

    // Pin lock view
//    implementation ("com.github.aritraroy:PinLockView:v2.1.0")

    // Calendar view
    implementation("com.github.prolificinteractive:material-calendarview:2.0.1")



}