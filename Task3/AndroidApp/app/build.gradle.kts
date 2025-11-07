plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.compiler)
    kotlin("plugin.serialization") version "2.2.20"
}

android {
    namespace = "com.example.speedray"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.speedray"
        minSdk = 31
        targetSdk = 36
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures{
        dataBinding =true
        compose = true
    }
}


dependencies {



    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //http3 for websocket and serialisation
    implementation("com.squareup.okhttp3:okhttp:5.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

    // Room dependencies
    val roomVersion = "2.8.0"
    implementation("androidx.room:room-runtime:$roomVersion")

    // If this project uses any Kotlin source, use Kotlin Symbol Processing (KSP)
    // See Add the KSP plugin to your project
    ksp("androidx.room:room-compiler:$roomVersion")


    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$roomVersion")

    // optional - RxJava2 support for Room
    implementation("androidx.room:room-rxjava2:$roomVersion")

    // optional - RxJava3 support for Room
    implementation("androidx.room:room-rxjava3:$roomVersion")

    // optional - Guava support for Room, including Optional and ListenableFuture
    implementation("androidx.room:room-guava:$roomVersion")

    // optional - Test helpers
    testImplementation("androidx.room:room-testing:$roomVersion")

    // optional - Paging 3 Integration
    implementation("androidx.room:room-paging:$roomVersion")


    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")


    //Compose dependecies
    val composeBom = platform("androidx.compose:compose-bom:2025.09.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("androidx.activity:activity-compose:1.11.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")





    // added by following : https://www.geeksforgeeks.org/kotlin/testing-room-database-in-android-using-junit/
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.1")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    testImplementation("androidx.arch.core:core-testing:2.1.0")
    testImplementation("junit:junit")
    testImplementation("com.google.truth:truth:1.1.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.4")
    testImplementation("org.robolectric:robolectric:4.5.1")
    androidTestImplementation ("androidx.test.ext:junit-ktx:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
    androidTestImplementation ("com.google.truth:truth:1.1.2")
    androidTestImplementation ("androidx.arch.core:core-testing:2.1.0")
    androidTestImplementation ("androidx.test:rules:1.3.0")
    androidTestImplementation ("androidx.test:runner:1.3.0")
    androidTestImplementation ("androidx.test:core-ktx:1.3.0")

    val nav_version = "2.9.5"

    implementation("androidx.navigation:navigation-compose:$nav_version")



}