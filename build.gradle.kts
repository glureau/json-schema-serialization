plugins {
  kotlin("multiplatform") version "1.7.20"
  id("com.android.library") version "7.0.1"
  kotlin("plugin.serialization") version "1.7.20"
  id("org.jetbrains.dokka") version "1.7.20"
  `maven-publish`
  id("org.ajoberstar.git-publish") version "3.0.1"
  id("org.ajoberstar.grgit") version "4.1.1"
}

repositories {
  mavenCentral()
  google()
}

android {
  buildToolsVersion = "33.0.1"
  compileSdk = 33
  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

  defaultConfig {
    minSdk = 21
    targetSdk = 33
  }
}

kotlin {
  jvm {
    compilations.all {
      kotlinOptions.jvmTarget = "1.8"
    }

    testRuns.all {
      executionTask {
        useJUnitPlatform()
      }
    }
  }

  js(BOTH) {
    nodejs {
      testTask {
        with(compilation) {
          kotlinOptions {
            moduleKind = "commonjs"
          }
        }
      }
    }
  }

  mingwX64()
  linuxX64()
  macosX64()
  ios()
  iosSimulatorArm64()
  android { publishLibraryVariants("release", "debug") }

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(kotlin("reflect"))
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
      }
    }

    val commonTest by getting {
      dependencies {
        implementation(kotlin("test-common"))
        implementation(kotlin("test-annotations-common"))
      }
    }

    val jvmMain by getting
    val jvmTest by getting {
      dependencies {
        implementation(kotlin("test-junit5"))
        runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.0.0")
      }
    }

    val jsMain by getting
    val jsTest by getting {
      dependencies {
        implementation(kotlin("test-js"))
      }
    }

    val nativeMain by creating {
      dependsOn(commonMain)
    }

    val nativeTest by creating {
      dependsOn(commonTest)
    }

    val mingwX64Main by getting {
      dependsOn(nativeMain)
    }

    val mingwX64Test by getting {
      dependsOn(nativeTest)
    }

    val linuxX64Main by getting {
      dependsOn(nativeMain)
    }

    val linuxX64Test by getting {
      dependsOn(nativeTest)
    }

    val macosX64Main by getting {
      dependsOn(nativeMain)
    }

    val macosX64Test by getting {
      dependsOn(nativeTest)
    }


    val iosMain by getting
    val iosTest by getting

    val iosSimulatorArm64Main by getting {
      dependsOn(iosMain)
    }

    val iosSimulatorArm64Test by getting {
      dependsOn(iosMain)
      dependsOn(iosTest)
    }

    all {
      languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
    }
  }
}

val gitUser = System.getenv("GIT_USER")
val gitPassword = System.getenv("GIT_PASSWORD")
if (gitUser != null && gitPassword != null) {
  System.setProperty("org.ajoberstar.grgit.auth.username", gitUser)
  System.setProperty("org.ajoberstar.grgit.auth.password", gitPassword)
}

tasks.create<Delete>("cleanMavenLocalArtifacts") {
  delete = setOf("$buildDir/mvn-repo/")
}

tasks.create<Sync>("copyMavenLocalArtifacts") {
  group = "publishing"
  dependsOn("publishToMavenLocal")

  val userHome = System.getProperty("user.home")
  val groupDir = project.group.toString().replace('.', '/')
  val localRepository = "$userHome/.m2/repository/$groupDir/"

  println("localRepository=$localRepository")
  from(localRepository) {
    include("*/${project.version}/**")
  }

  into("$buildDir/mvn-repo/$groupDir/")
}

gitPublish {
  repoUri.set("git@github.com:glureau/json-schema-serialization.git")
  branch.set("mvn-repo")
  contents.from("$buildDir/mvn-repo")
  preserve { include("**") }
  val head = grgit.head()
  println("COMMIT: ${head.abbreviatedId}: ${project.version} : ${head.fullMessage}")
  commitMessage.set("${head.abbreviatedId}: ${project.version} : ${head.fullMessage}")
}
tasks["copyMavenLocalArtifacts"].dependsOn("cleanMavenLocalArtifacts")
tasks["gitPublishPush"].dependsOn("copyMavenLocalArtifacts")
