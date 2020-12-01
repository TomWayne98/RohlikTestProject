
plugins {
    id(Plugins.androidApplication)
    id(Plugins.kotlinAndroid)
   // kotlin(Plugins.kapt)
}

android {
    compileSdkVersion(Versions.targetSdk)

    defaultConfig {
        applicationId = ApplicationId.id
        minSdkVersion(Versions.minSdk)
        targetSdkVersion(Versions.targetSdk)
        versionCode = Releases.versionCode
        versionName = Releases.versionName
    }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("release") {
            isMinifyEnabled = true
            isDebuggable = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    // without these lines the build would crash for older devices
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(project(Modules.core))
    implementation(project(Modules.homescreen))
    implementation(project(Modules.prefs))
    implementation(project(Modules.architecture))
    implementation(project(Modules.db))

    implementation(Libraries.kotlinStd)
    implementation(Libraries.kotlinKtx)

    implementation(Libraries.appcompat)
    implementation(Libraries.material)
    implementation(Libraries.annotation)
    implementation(Libraries.constraint)
    implementation(Libraries.livedataKtx)
    implementation(Libraries.viewModel)

    implementation(Libraries.navigationFragment)
    implementation(Libraries.navigationUi)

    implementation(Libraries.koinViewModel)
    implementation(Libraries.koinAndroid)

    implementation(Libraries.timber)

    implementation(Libraries.retrofit)
    implementation(Libraries.retrofitGson)
    implementation(Libraries.retrofitCoroutines)

    implementation(Libraries.roomRuntime)
}