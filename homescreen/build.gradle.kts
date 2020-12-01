plugins {
    id(Plugins.androidLibrary)
    id(Plugins.kotlinAndroid)
    id(Plugins.kotlinExtensions)
}

android {
    compileSdkVersion(Versions.targetSdk)

    defaultConfig {
        minSdkVersion(Versions.minSdk)
        targetSdkVersion(Versions.targetSdk)
        versionCode = Releases.versionCode
        versionName = Releases.versionName
    }

    lintOptions {
        ///Users/josef/.gradle/caches/modules-2/files-2.1/io.grpc/grpc-core/1.16.1/8a938ece0ad8d8bf77d790c502ba51ebec114aa9/grpc-core-1.16.1.jar: Error: Invalid package reference in library; not included in Android: javax.naming.directory. Referenced from io.grpc.internal.JndiResourceResolverFactory.JndiResourceResolver. [InvalidPackage]
        disable("InvalidPackage") // In order not to check stupid gradle cache
    }

    buildTypes {
        getByName("debug") {
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(project(Modules.core))
    implementation(project(Modules.architecture))
    implementation(project(Modules.ui))

    implementation(Libraries.appcompat)
    implementation(Libraries.constraint)
    implementation(Libraries.navigationUi)
    implementation(Libraries.navigationFragment)
    implementation(Libraries.koinViewModel)
}
