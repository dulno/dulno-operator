plugins {
  id("java")
  id("maven-publish")
  id("io.freefair.lombok") version "8.14"
  id("org.springframework.boot") version "3.5.0"
}

group = "com.dulno"
version = "1.0.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

publishing {
  publications {
    create<MavenPublication>("library") {
      from(components["java"])
    }
  }
  repositories {
    maven {
      url = uri("https://git.dulno.com/api/v4/projects/53/packages/maven")
      credentials(HttpHeaderCredentials::class) {
        name = "Private-Token"
        value = System.getenv("DULNO_GITLAB_PRIVATE_TOKEN") ?:
          findProperty("dulnoGitlabPrivateToken") as String?
      }
      authentication {
        create("header", HttpHeaderAuthentication::class)
      }
    }
  }
}

repositories {
  mavenCentral()
  maven {
    url = uri("https://git.dulno.com/api/v4/projects/51/packages/maven")
    credentials(HttpHeaderCredentials::class) {
      name = "Private-Token"
      value = System.getenv("DULNO_GITLAB_PRIVATE_TOKEN") ?:
        findProperty("dulnoGitlabPrivateToken") as String?
    }
    authentication {
      create("header", HttpHeaderAuthentication::class)
    }
  }
}

dependencies {
  testImplementation(platform("org.junit:junit-bom:5.13.1"))
  testImplementation("org.junit.jupiter:junit-jupiter:5.13.1")

  implementation("com.dulno:core:1.0.0-SNAPSHOT")

  implementation("com.google.inject:guice:7.0.0")

  implementation("com.google.guava:guava:33.4.8-jre")

  implementation("org.projectlombok:lombok:1.18.38")
  annotationProcessor("org.projectlombok:lombok:1.18.38")
  testImplementation("org.projectlombok:lombok:1.18.38")
  testAnnotationProcessor("org.projectlombok:lombok:1.18.38")

  implementation("org.json:json:20250517")

  implementation("org.apache.tomcat.embed:tomcat-embed-core:11.0.8")

  implementation("io.netty:netty-all:4.2.2.Final")

  implementation("io.kubernetes:client-java:23.0.0")
}

tasks.test {
  useJUnitPlatform()
}

tasks.bootJar {
  mainClass = "com.dulno.operator.OperatorApplication"
}