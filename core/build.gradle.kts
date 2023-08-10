@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `java-library`
    alias(libs.plugins.java.qa)
}

group = "it.unibo.ds"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.fabric.chaincode.shim)
    implementation(libs.genson)
    implementation(libs.json)
    testImplementation(libs.junit.api)
    testImplementation(libs.junit.engine)
}

tasks.test {
    useJUnitPlatform()
}
