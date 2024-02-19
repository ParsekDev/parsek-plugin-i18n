plugins {
    java
    kotlin("jvm") version "1.9.20"
    kotlin("kapt") version "1.9.20"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

val pf4jVersion: String by project
val vertxVersion: String by project
val handlebarsVersion: String by project
val bootstrap = project.findProject("bootstrap") as Boolean? ?: false

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    if (bootstrap) {
        compileOnly(project(mapOf("path" to ":Parsek")))
    } else {
        compileOnly("com.github.StatuParsek:Parsek:main-SNAPSHOT")
    }

    compileOnly(kotlin("stdlib-jdk8"))

    compileOnly("org.pf4j:pf4j:${pf4jVersion}")
    kapt("org.pf4j:pf4j:${pf4jVersion}")

    compileOnly("io.vertx:vertx-web:$vertxVersion")
    compileOnly("io.vertx:vertx-lang-kotlin:$vertxVersion")
    compileOnly("io.vertx:vertx-lang-kotlin-coroutines:$vertxVersion")
    compileOnly("io.vertx:vertx-jdbc-client:$vertxVersion")
    compileOnly("io.vertx:vertx-json-schema:$vertxVersion")
    compileOnly("io.vertx:vertx-web-validation:$vertxVersion")

    // https://mvnrepository.com/artifact/com.github.jknack/handlebars
    compileOnly("com.github.jknack:handlebars:$handlebarsVersion")
}

tasks.named("jar").configure {
    enabled = false
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}
