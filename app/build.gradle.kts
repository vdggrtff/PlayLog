import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id ("kotlin-parcelize")
    kotlin("plugin.serialization") version "2.0.21"
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}
val rawgApiKey = localProperties.getProperty("RAWG_API_KEY") ?: ""
val rawgUrlKey = localProperties.getProperty("RAWG_URL") ?: ""
val supabaseUrlKey = localProperties.getProperty("SUPABASE_URL") ?: ""
val supabaseAnonKey = localProperties.getProperty("SUPABASE_ANON_KEY") ?: ""

val geminiApiKey = localProperties.getProperty("GEMINI_API_KEY") ?: ""

val raUser = localProperties.getProperty("RA_USER") ?: ""

val raApiKey = localProperties.getProperty("RA_API_KEY") ?: ""

val igdbClientId = localProperties.getProperty("IGDB_CLIENT_ID") ?: ""

val igdbClientSecret = localProperties.getProperty("IGDB_CLIENT_SECRET") ?: ""


android {
    namespace = "com.vdggrtf.playlog"
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.vdggrtf.playlog"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "RAWG_API_KEY", "\"$rawgApiKey\"")
        buildConfigField("String", "RAWG_URL", "\"$rawgUrlKey\"")
        buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrlKey\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"$supabaseAnonKey\"")
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
        buildConfigField("String", "RA_USER", "\"$raUser\"")
        buildConfigField("String", "RA_API_KEY", "\"$raApiKey\"")
        buildConfigField("String", "IGDB_CLIENT_ID", "\"$igdbClientId\"")
        buildConfigField("String", "IGDB_CLIENT_SECRET", "\"$igdbClientSecret\"")
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
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.compose.material.icons.extended)
    implementation("com.google.dagger:hilt-android:2.60.1")
    ksp("com.google.dagger:hilt-android-compiler:2.60.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.4.0")
    implementation("androidx.compose.runtime:runtime-livedata:1.11.4")
    implementation("androidx.navigation:navigation-compose:2.9.8")
    implementation("com.google.code.gson:gson:2.14.0")
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation("com.squareup.okhttp3:okhttp:5.4.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.4.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:5.4.0")
    implementation("androidx.datastore:datastore-preferences:1.2.1")
    testImplementation("io.mockk:mockk:1.14.11")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.11.0")
    implementation("androidx.room:room-runtime:2.8.4")
    implementation("androidx.room:room-ktx:2.8.4")
    ksp("androidx.room:room-compiler:2.8.4")

    val supabaseVersion = "2.4.2"
    implementation("io.github.jan-tennert.supabase:gotrue-kt:$supabaseVersion")
    implementation("io.github.jan-tennert.supabase:postgrest-kt:$supabaseVersion")

    val ktorVersion = "2.3.9"
    implementation("io.ktor:ktor-client-android:$ktorVersion")

    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
    implementation("androidx.browser:browser:1.10.0")
    implementation("dev.shreyaspatil:capturable:3.0.1")
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("io.coil-kt:coil-gif:2.7.0")
}