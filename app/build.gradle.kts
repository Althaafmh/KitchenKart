// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.PROJECT.kitchenkart"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.PROJECT.kitchenkart"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Correct way to import the Firebase BOM
    implementation(platform("com.google.firebase:firebase-bom:34.1.0"))
    // AndroidX Core Libraries with versions
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("com.google.android.material:material:1.12.0")

    // Firebase dependencies
    implementation("com.google.firebase:firebase-auth-ktx:23.0.0")
    implementation("com.google.firebase:firebase-firestore-ktx:25.0.0")
    implementation("com.google.firebase:firebase-storage-ktx:21.0.0")
    implementation("com.google.firebase:firebase-database-ktx:21.0.0")
   // implementation("com.google.firebase:firebase-appcheck-playintegrity-ktx:17.1.1") // Removed due to a build issue
    implementation("com.google.firebase:firebase-messaging-ktx:24.0.0")


    // Other dependencies with versions
    // implementation("com.google.android.gms:play-services-auth:21.2.0") // Removed to avoid potential SecurityException
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Room dependencies
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")

    implementation("com.cloudinary:cloudinary-android:3.0.2")

    // Testing Libraries with versions
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
