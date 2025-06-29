plugins {
    id("com.android.library") version "8.6.0"
    kotlin("android") version "1.8.0"

    id("im.agg.android12-system-jars") version "1.0.2"
}

android {
    namespace = "com.penumbraos.appprocessmocks"
    //noinspection GradleDependency
    compileSdk = 35

    defaultConfig {
        minSdk = 32
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
}
