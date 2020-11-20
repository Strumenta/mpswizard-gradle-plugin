group = "com.strumenta"
version = "1.0.0"
description = "An opinionated plugin to simplify the life of MPS developers"

val kotlinVersion = "1.4.10"

plugins {
  kotlin("jvm") version "1.4.10"
  id("java-gradle-plugin")
  id("maven-publish")
  id("com.gradle.plugin-publish") version "0.11.0"
}

pluginBundle {
  website = "https://github.com/Strumenta/mpswizard-gradle-plugin"
  vcsUrl = "https://github.com/Strumenta/mpswizard-gradle-plugin.git"
  tags = listOf("Jetbrains MPS")
}


gradlePlugin {
  plugins {
    create("mpswizard") {
      id = "com.strumenta.mpswizard"
      displayName = "MPS wizard gradle plugin"
      description = project.description
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