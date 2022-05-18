/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

buildscript {
  repositories {
    mavenCentral()
    maven {
      // WT published releases
      setUrl("https://s3.eu-central-1.amazonaws.com/artifacts.wisetime.com/mvn2/plugins")
      content {
        includeGroup("io.wisetime")
      }
    }
  }
  dependencies {
    // https://github.com/GoogleContainerTools/jib/issues/1018
    classpath("org.apache.httpcomponents:httpclient:4.5.12") {
      setForce(true)
    }
  }
}

plugins {
  java
  idea
  id("application")
  id("maven-publish")
  id("io.freefair.lombok") version "6.3.0"
  id("fr.brouillard.oss.gradle.jgitver") version "0.9.1"
  id("com.google.cloud.tools.jib") version "3.1.2"
  id("com.github.ben-manes.versions") version "0.38.0"
  id("io.wisetime.versionChecker") version "10.11.84"
}

apply(from = "$rootDir/gradle/conf/checkstyle.gradle")
apply(from = "$rootDir/gradle/conf/jacoco.gradle")


java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(11))
    vendor.set(JvmVendorSpec.ADOPTOPENJDK)
    implementation.set(JvmImplementation.J9)
  }
  consistentResolution {
    useCompileClasspathVersions()
  }
}

group = "io.wisetime"
application {
  mainClass.set("io.wisetime.connector.patrawin.ConnectorLauncher")
}

jib {
  val targetArch = project.properties["targetArch"] ?: ""
  if (targetArch == "arm64v8") {
    from {
      image = "arm64v8/openjdk:11.0.8-jdk-buster"
    }
    to {
      project.afterEvaluate { // <-- so we evaluate version after it has been set
        image = "wisetime/wisetime-patrawin-connector-arm64v8:${project.version}"
      }
    }
  } else {
    from {
      image = "gcr.io/wise-pub/connect-java-11-j9@sha256:98ec5f00539bdffeb678c3b4a34c07c77e4431395286ecc6a083298089b3d0ec"
    }
    to {
      project.afterEvaluate { // <-- so we evaluate version after it has been set
        image = "wisetime/wisetime-patrawin-connector:${project.version}"
      }
    }
  }
}

repositories {
  mavenCentral()
  maven {
    // WiseTime artifacts
    setUrl("https://s3.eu-central-1.amazonaws.com/artifacts.wisetime.com/mvn2/releases")
    content {
      includeGroup("io.wisetime")
    }
  }
}

tasks.withType(com.google.cloud.tools.jib.gradle.JibTask::class.java) {
  dependsOn(tasks.compileJava)
}

val taskRequestString = gradle.startParameter.taskRequests.toString()
if (taskRequestString.contains("dependencyUpdates")) {
  // add exclusions for reporting on updates and vulnerabilities
  apply(from = "$rootDir/gradle/versionPluginConfig.gradle")
}
val junitVersion = "5.7.0"

dependencies {
  implementation("io.wisetime:wisetime-connector:4.1.4")
  implementation("org.apache.commons:commons-lang3:3.12.0")
  implementation("org.apache.httpcomponents:httpcore:4.4.14")
  implementation("com.fasterxml.jackson.core:jackson-databind:2.12.5")
  implementation("org.freemarker:freemarker:2.3.31")
  implementation("org.springframework.boot:spring-boot-starter-validation:2.5.4")
  implementation("commons-codec:commons-codec:1.15")
  implementation("com.google.inject:guice:5.0.1") {
    exclude(group = "com.google.guava", module = "guava")
  }
  implementation("com.google.guava:guava:30.1-jre")

  implementation("org.codejargon:fluentjdbc:1.8.6")
  implementation("com.zaxxer:HikariCP:3.3.1")
  implementation("com.microsoft.sqlserver:mssql-jdbc:8.2.2.jre8")
  implementation("com.sun.mail:javax.mail:1.6.2")
  implementation("joda-time:joda-time:${io.wisetime.version.model.LegebuildConst.JODA_TIME}")
  implementation("org.slf4j:jcl-over-slf4j:${io.wisetime.version.model.LegebuildConst.SLF4J}")

  testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
  testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")

  testImplementation("org.mockito:mockito-core:3.5.13")
  testImplementation("org.assertj:assertj-core:3.17.2")
  testImplementation("org.flywaydb:flyway-core:7.5.4")
  testImplementation("com.github.javafaker:javafaker:0.17.2") {
    exclude(group = "org.apache.commons", module = "commons-lang3")
  }
  testImplementation("org.immutables:value:2.7.5:annotations")
  testImplementation("io.wisetime:wisetime-test-support:${io.wisetime.version.model.LegebuildConst.WT_TEST_SUPPORT}")
}

configurations.all {
  resolutionStrategy {
    eachDependency {
      if (requested.group == "com.fasterxml.jackson.core") {
        useVersion("2.12.3")
        because("use consistent version for all transitive dependencies")
      }
      if (requested.name == "commons-lang3") {
        useVersion("3.12.0")
        because("use consistent version for all transitive dependencies")
      }
      if (requested.group == "org.slf4j") {
        useVersion("1.7.32")
        because("use consistent version for all transitive dependencies")
      }
    }
  }
}

tasks.test {
  useJUnitPlatform()
  testLogging {
    // "passed", "skipped", "failed"
    events("skipped", "failed")
    exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
  }
}

tasks.clean {
  delete("${projectDir}/out")
}

jgitver {
  autoIncrementPatch(false)
  strategy(fr.brouillard.oss.jgitver.Strategies.PATTERN)
  versionPattern("\${meta.CURRENT_VERSION_MAJOR}.\${meta.CURRENT_VERSION_MINOR}.\${meta.COMMIT_DISTANCE}")
  regexVersionTag("v(\\d+\\.\\d+(\\.0)?)")
}

publishing {
  repositories {
    maven {
      setUrl("s3://artifacts.wisetime.com/mvn2/releases")
      authentication {
        val awsIm by registering(AwsImAuthentication::class)
      }
    }
  }

  publications {
    register("mavenJava", MavenPublication::class) {
      artifactId = "wisetime-patrawin-connector"
      from(components["java"])
    }
  }
}

tasks.register < DefaultTask > ("printVersionStr") {
  doLast {
    println("${project.version}")
  }
}
