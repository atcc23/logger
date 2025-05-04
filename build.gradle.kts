import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `maven-publish`
    id("com.android.library")
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id("jacoco")
    id("org.sonarqube")
}

apply {
    from("$rootDir/jacoco.gradle")
}
val createJacocoTask: groovy.lang.Closure<Any> by ext

val versionCode = ConfigData.version
val artifactName = "logger"

val coveragePath = "testReleaseUnitTestCoverage"

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        compilerOptions.freeCompilerArgs.set(
            listOf(
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.coroutines.FlowPreview",
                "-opt-in=kotlinx.coroutines.ObsoleteCoroutinesApi"
            )
        )
    }
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
}

// Because the components are created only during the afterEvaluate phase, you must
// configure your publications using the afterEvaluate() lifecycle method.
afterEvaluate {
    publishing {

        repositories {
            maven {
                setUrl(ConfigData.artifactoryUrl)
                isAllowInsecureProtocol = true

                credentials {
                    username = ConfigData.username
                    password = ConfigData.password
                }
            }
        }

        publications {
            create<MavenPublication>("release") {
                // Applies the component for the release build variant.
                from(components["release"])

                artifact(sourcesJar.get())

                // You can then customize attributes of the publication as shown below.
                groupId = "com.atcc"
                artifactId = "$artifactName-RELEASE"
                version = versionCode

                pom {
                    name.set("Atcc logger Library")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("jakal23")
                            name.set("Vahe Gharibyan")
                            email.set("vahe.gharibyan.23@gmail.com")
                        }
                    }
                }
            }
            create<MavenPublication>("debug") {
                // Applies the component for the release build variant.
                from(components["debug"])

                artifact(sourcesJar.get())
                // You can then customize attributes of the publication as shown below.
                groupId = "com.atcc"
                artifactId = "$artifactName-SNAPSHOT"
                version = versionCode
            }
        }
    }

    android.buildTypes
        .map { type -> type.name }
        .forEach { buildTypeName ->
            createJacocoTask("", buildTypeName)
        }
}

sonarqube {
    properties {
        property("sonar.projectName", "Logger")
        property("sonar.projectKey", "com.atcc.logger")
        property("sonar.projectVersion", versionCode)
        property("sonar.language", "kotlin")
        property("sonar.sources", "src/main/java/")
        property("sonar.core.codeCoveragePlugin", "jacoco")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            "${project.buildDir}/reports/jacoco/${coveragePath}/${coveragePath}.xml"
        )

        property("sonar.login", ConfigData.username)
        property("sonar.password", ConfigData.password)
        property("sonar.host.url", ConfigData.sonarqubeUrl)
    }
}

android {
    compileSdk = ConfigData.compileSdkVersion
    namespace = "com.atcc.logger"

    defaultConfig {
        minSdk = ConfigData.minSdkVersion


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    testImplementation(libs.junit)
}