group = "com.strumenta"
version = "1.0.0-SNAPSHOT"

val kotlinVersion = "1.4.10"

plugins {
  kotlin("jvm") version "1.4.10"
  id("java-gradle-plugin")
  id("maven-publish")
  id("com.gradle.plugin-publish") version "0.11.0"
}


gradlePlugin {
  plugins {
    create("mpswizard") {
      id = "com.strumenta.mpswizard"
      displayName = "MPS wizard gradle plugin"
      description = "This should make easy to use MPS with gradle"
      implementationClass = "com.strumenta.mpswizard.MpsWizardPlugin"
    }
  }
}

repositories {
  jcenter()
}

dependencies {
  implementation(kotlin("stdlib", "$kotlinVersion"))
}