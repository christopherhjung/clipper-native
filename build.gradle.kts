import org.gradle.kotlin.dsl.execution.ProgramText.Companion.from

plugins {
    java
    `maven-publish`
}


group = "com.angusj.clipper"
version = "0.1.0"

repositories {
    mavenCentral()
    mavenLocal()
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}


publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
    repositories {
        mavenLocal()
    }
}


tasks.register<Exec>("create-resource-folder") {
    workingDir("build")
    commandLine("mkdir", "-p","resources/main/macos/")
}

tasks.register<Exec>("cpp-libs-folder") {
    workingDir("build")
    commandLine("mkdir", "-p", "cppLibs")
}

val osLibs = listOf("xxx") // later for iterating over all os

for(os in osLibs){
    tasks.register<Exec>("cmake-cpp-libs-$os") {
        dependsOn("cpp-libs-folder")
        workingDir("build/cppLibs")
        commandLine("cmake", "../../")
    }

    tasks.register<Exec>("make-cpp-libs-$os") {
        dependsOn("cmake-cpp-libs-$os")
        workingDir("build/cppLibs")
        commandLine("make")
    }

    tasks.register<Copy>("copy-cpp-libs-$os") {
        dependsOn("make-cpp-libs-$os", "create-resource-folder")
        from("build/cppLibs/libclipper-native.dylib"){
            into("resources/main/macos/")
        }
        destinationDir = file("build/")
    }

    tasks.withType<ProcessResources> {
        dependsOn("copy-cpp-libs-$os")
    }
}


//tasks.withType<Assemble> {
/*tasks.withType<ProcessResources> {
    dependsOn(osLibs.map { "copy-cpp-libs-$it" })
}*/

dependencies {
    testImplementation("junit", "junit", "4.12")
}
