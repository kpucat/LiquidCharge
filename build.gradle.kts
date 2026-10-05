plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.mars.liquidcharge"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.mars.liquidcharge"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "0.1"
    }
}
dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
}
