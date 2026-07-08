plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.enrique.cryptowallet"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.enrique.cryptowallet"
        minSdk = 24
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

    // --- ARQUITETURA MVVM & LIFECYCLE ---
    // ViewModel e LiveData: Essenciais para o MVVM funcionar e a tela observar os dados
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.0")
    implementation("androidx.activity:activity-ktx:1.9.0")

    // --- RETROFIT (Para conectar com a API CoinGecko) ---
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0") // Converte o JSON da API para objetos Kotlin

    // --- ROOM (Para o CRUD / Banco de dados local) ---
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // --- COROUTINES (Para processar dados em segundo plano sem travar o app) ---
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // --- COIL (Para carregar as imagens/ícones das moedas direto da internet) ---
    implementation("io.coil-kt:coil:2.6.0")

    // --- SWIPE REFRESH (Para puxar a tela e atualizar) ---
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

}