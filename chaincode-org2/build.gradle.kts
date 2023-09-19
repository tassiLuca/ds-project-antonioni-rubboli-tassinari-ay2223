import java.net.URI

plugins {
    application
}

application {
    mainClass.set("org.hyperledger.fabric.contract.ContractRouter")
}

repositories {
    maven { url = URI("https://jitpack.io") }
}

dependencies {
    api(project(":presentation"))
    implementation(libs.fabric.chaincode.shim)
    implementation(libs.genson)
    implementation(libs.json)
    implementation(libs.commons.lang)
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.3.0")
    implementation(project(mapOf("path" to ":chaincode-org1")))
    testImplementation(libs.assertj.core)
    testImplementation(libs.mockito.core)
}
