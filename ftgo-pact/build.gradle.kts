plugins {
    `java-library`
    id("org.jetbrains.kotlin.jvm")
}

group = "me.jangjunha.ftgo"
version = "unspecified"

java {
    registerFeature("provider") {
        usingSourceSet(sourceSets["main"])
    }
}

repositories {
    mavenCentral()
}

dependencies {
    "providerApi"("au.com.dius.pact:provider:4.6.2")
}

tasks.test {
    useJUnitPlatform()
}
