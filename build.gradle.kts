plugins {
	java
	id("org.springframework.boot") version "4.0.6"
	id("io.spring.dependency-management") version "1.1.7"
    id("org.graalvm.buildtools.native") version "0.10.6"
}

group = "com.kody.coinsec"
version = "2.1.1"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

graalvmNative {
	metadataRepository {
		enabled = true
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("org.postgresql:postgresql")
	implementation("org.flywaydb:flyway-core")
	runtimeOnly("org.flywaydb:flyway-database-postgresql")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-webmvc-test")
	testCompileOnly("org.projectlombok:lombok")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testAnnotationProcessor("org.projectlombok:lombok")

    implementation("cn.dev33:sa-token-spring-boot4-starter:1.45.0")
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.3")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
