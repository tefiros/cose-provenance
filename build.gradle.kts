plugins {
    `java-library` // This applies the Java library plugin, which is what you need for creating a JAR library
    `maven-publish` // This plugin allows you to publish your library to a Maven repository (local or remote)
}

repositories {
    mavenLocal() // This tells Gradle to look in the local Maven repository for dependencies
    mavenCentral() // This tells Gradle to also look in Maven Central if the dependency isn't in the local Maven repo
}

dependencies {
    // These are the dependencies you want for your library.
    // You can leave this or add/remove dependencies as necessary for your code.
    api(libs.com.augustcellars.cose.cose.java)
    api(libs.org.bouncycastle.bcpkix.jdk18on)
    api(libs.org.apache.santuario.xmlsec)
    api(libs.org.jdom.jdom2)
    api(libs.com.google.code.gson.gson)
}

group = "com.telefonica.api" // Define your group (package) structure here
version = "0.0.2" // Version of your library
description = "Provenance API Library" // Brief description of the library
java.sourceCompatibility = JavaVersion.VERSION_1_8 // Make sure the source compatibility matches your Java version

// This block will generate a JAR file that can be used as a library
publishing {
    publications {
        // Create a Maven publication for your library
        create<MavenPublication>("maven") {
            from(components["java"]) // This tells Gradle to publish the Java components (i.e., the JAR) of the project
            groupId = "com.telefonica.api" // Group ID of your library
            artifactId = "provenance-api" // Artifact ID (name) of your library
            version = "0.0.2" // Version of your library (same as defined above)

            // You can also add additional metadata or custom configuration to the published artifact if needed
            pom {
                name.set("Provenance API Library") // Name of the library
                description.set("A library for COSE Provenance Signatures") // Description
            }
        }
    }

    repositories {
        mavenLocal() // Publish the library to your local Maven repository
        // You can add remote repositories if you plan to publish it to a remote Maven repo later
    }
}


