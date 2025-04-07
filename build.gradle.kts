repositories {
  mavenCentral()
}

application {
  mainClass = "ru.nskopt.App"
}

plugins {
  // core
  id("application")
  id("java")
  id("jacoco")

  // community
  id("org.springframework.boot") version "3.4.2"
  id("io.spring.dependency-management") version "1.1.7"
  id("com.diffplug.spotless") version "6.25.0"
  id("com.adarshr.test-logger") version "4.0.0"
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.4")
  implementation("net.coobird:thumbnailator:0.4.20")
  implementation("org.sejda.imageio:webp-imageio:0.1.6")

  implementation("org.springframework.boot:spring-boot-starter-security:3.4.3")
  implementation("io.jsonwebtoken:jjwt-api:0.12.6")
  runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
  runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")


  implementation("org.projectlombok:lombok-mapstruct-binding:0.2.0")
  compileOnly("org.projectlombok:lombok-mapstruct-binding:0.2.0")
  runtimeOnly("org.projectlombok:lombok-mapstruct-binding:0.2.0")


  implementation("org.projectlombok:lombok-mapstruct-binding:0.2.0")
  compileOnly("org.projectlombok:lombok-mapstruct-binding:0.2.0")
  runtimeOnly("org.projectlombok:lombok-mapstruct-binding:0.2.0")


  compileOnly("org.projectlombok:lombok:1.18.30")
  annotationProcessor("org.projectlombok:lombok:1.18.30")

  implementation("org.mapstruct:mapstruct:1.5.5.Final")
  annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

  annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")


  runtimeOnly("org.postgresql:postgresql")

  compileOnly("org.projectlombok:lombok")
  annotationProcessor("org.projectlombok:lombok")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
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

tasks.withType<Test> {
  useJUnitPlatform()
  finalizedBy(tasks.jacocoTestReport)
}

testlogger {
    theme = com.adarshr.gradle.testlogger.theme.ThemeType.STANDARD
    showExceptions = true
    showStackTraces = true
    showFullStackTraces = false
    showCauses = true
    slowThreshold = 2000
    showSummary = true
    showSimpleNames = false
    showPassed = true
    showSkipped = true
    showFailed = true
    showStandardStreams = false
    showPassedStandardStreams = true
    showSkippedStandardStreams = true
    showFailedStandardStreams = true
    logLevel = LogLevel.LIFECYCLE
}
