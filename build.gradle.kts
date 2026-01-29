plugins {
    id("java-library")
}

group = "net.tzimom"
version = "1.0.0"

repositories {
    mavenCentral()

    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.11-R0.1-SNAPSHOT")
    implementation("net.kyori:adventure-api:4.26.1")
    implementation("net.kyori:adventure-text-serializer-legacy:4.26.1")
    implementation("net.kyori:adventure-text-serializer-gson:4.26.1")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}
