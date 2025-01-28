plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    //
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "ru.nskopt.App"
}