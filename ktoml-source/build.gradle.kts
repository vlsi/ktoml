import com.akuleshov7.buildutils.configureSigning
import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.akuleshov7.buildutils.publishing-configuration")
    id("com.saveourtool.diktat")
}

kotlin {
    explicitApi()

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }

    // building jvm task only on windows
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    mingwX64()
    linuxX64()
    macosX64()
    macosArm64()
    ios()
    iosSimulatorArm64()

    sourceSets {
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }

        val commonMain by getting {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-serialization-core:${Versions.SERIALIZATION}")
                implementation("com.squareup.okio:okio:${Versions.OKIO}")
                implementation("org.jetbrains.kotlin:kotlin-stdlib:${Versions.KOTLIN}")
                implementation(project(":ktoml-core"))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val jvmTest by getting {
            dependsOn(commonTest)
            dependencies {
                implementation(kotlin("test-junit5"))
                implementation("org.junit.jupiter:junit-jupiter-engine:5.10.1")
            }
        }

        all {
            languageSettings.enableLanguageFeature("InlineClasses")
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.akuleshov7"
            artifactId = "ktoml-source"
            version = version
            from(components["kotlin"])
        }
    }
}

configureSigning()

tasks.withType<KotlinJvmTest> {
    useJUnitPlatform()
}
