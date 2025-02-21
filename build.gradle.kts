plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    api(libs.com.augustcellars.cose.cose.java)
    api(libs.org.bouncycastle.bcpkix.jdk18on)
    api(libs.org.apache.santuario.xmlsec)
    api(libs.org.jdom.jdom2)
    api(libs.com.google.code.gson.gson)

    // OpenFaaS dependencies
    implementation("com.openfaas:model:0.1.1")
    implementation("com.openfaas:entrypoint:0.1.0")

    // Add Jackson libraries
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.fasterxml.jackson.core:jackson-core:2.15.2")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.15.2")


    // To canonicalize in JCS schema JSON
    implementation(files("libs/java-json-canonicalization-1.1.jar"))

}

group = "com.telefonica.api"
version = "0.0.1-SNAPSHOT"
description = "provenance-api"
java.sourceCompatibility = JavaVersion.VERSION_1_8

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.register<JavaExec>("runExample") {
    mainClass.set("com.telefonica.cose.provenance.example.Signer")
    classpath = sourceSets.main.get().runtimeClasspath
    args = project.findProperty("execArgs")?.toString()?.split(",") ?: listOf()
}

tasks.withType<ProcessResources> {
    from("src/main/resources") {
        include("**/*") // Include all resources
    }
}

tasks.withType<ProcessResources> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE // Prevent duplicates from causing errors
}

tasks {
    // Configure the Shadow JAR plugin to create a fat JAR
    withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
        archiveBaseName.set("provenance-api") // Set the JAR base name
        archiveClassifier.set("")             // No classifier (empty means the main JAR)
        archiveVersion.set("")                // You can set this to the project version or leave it empty
        manifest {
            attributes(mapOf("Main-Class" to "com.telefonica.cose.provenance.example.Signer")) // Specify the main class
        }
    }

    build {
        dependsOn("shadowJar") // Ensure the fat JAR is built as part of the build process
    }
}



