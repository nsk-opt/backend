repositories {
  mavenCentral()
}

application {
  mainClass = "ru.nskopt.App"
}

plugins {
  application

  id("org.springframework.boot") version "3.4.2"
  id("io.spring.dependency-management") version "1.1.7"
  id("com.diffplug.spotless") version "6.25.0"
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.4")

  runtimeOnly("org.postgresql:postgresql")

  compileOnly("org.projectlombok:lombok")
  annotationProcessor("org.projectlombok:lombok")
}

configurations {
  compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
  }
}

spotless {
  java {
    googleJavaFormat()
    removeUnusedImports()
  }
}

repositories {
  mavenCentral()
}

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
  }
}

tasks.named("bootJar") {
  dependsOn("spotlessApply")
}

tasks.named("bootRun") {
  dependsOn("spotlessApply")
}

tasks.named("build") {
  dependsOn("spotlessApply")
}