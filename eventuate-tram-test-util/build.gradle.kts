plugins {
    id("org.jetbrains.kotlin.jvm")
}

group = "me.jangjunha.ftgo"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.eventuate.tram.sagas:eventuate-tram-sagas-orchestration-simple-dsl")
}
