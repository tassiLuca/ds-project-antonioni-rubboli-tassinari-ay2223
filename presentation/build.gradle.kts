@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `java-library`
    alias(libs.plugins.java.qa)
}

group = "it.unibo.ds"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":core"))
    implementation(libs.genson)
    testImplementation(libs.junit.api)
    testImplementation(libs.junit.engine)
}

tasks.test {
    useJUnitPlatform()
}
