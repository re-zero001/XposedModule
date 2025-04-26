@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.tasks.PackageAndroidArtifact

plugins {
    id("com.android.application")
    id("org.lsposed.lsplugin.resopt")
    id("org.lsposed.lsparanoid")
}

lsparanoid {
    seed = 10721
    classFilter = { true }
    includeDependencies = false
    variantFilter = { variant ->
        variant.buildType != "debug"
    }
}

android {
    namespace = "org.irena.xposedmodule"
    compileSdk = 36
    buildToolsVersion = "36.0.0"

    defaultConfig {
        applicationId = "org.irena.xposedmodule"
        minSdk = 27
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        setProperty("archivesBaseName", "xposedmodule")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            vcsInfo.include = false
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    // https://stackoverflow.com/a/77745844
    tasks.withType<PackageAndroidArtifact> {
        doFirst { appMetadata.asFile.orNull?.writeText("") }
    }

    lint {
        checkReleaseBuilds = false
        abortOnError = true
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

dependencies {
    compileOnly(libs.api)
}

afterEvaluate {
    tasks.register<Copy>("copyApkToReleaseDir") {
        dependsOn("assembleRelease")
        val releaseDir = File("${project.rootDir}/release")
        releaseDir.mkdirs()
        from(layout.buildDirectory.dir("outputs/apk/release")) {
            include("*.apk")
        }
        from(layout.buildDirectory.dir("outputs/apk/debug")) {
            include("*.apk")
        }
        into(releaseDir)
    }
    tasks.named("assembleRelease").configure {
        finalizedBy("copyApkToReleaseDir")
    }
    tasks.named("build") {
        dependsOn("copyApkToReleaseDir")
    }
}
