plugins {
	java
	id("org.springframework.boot") version "3.4.4"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("com.mysql:mysql-connector-j")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("com.fasterxml.jackson.core:jackson-databind")

	//JWT JWT는 토큰 표준이고 JJWT는 JWT를 처리하는 라이브러리
	//JWT 기능 사용을 위한 인터페이스
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	//API 기능을 실제로 동작하게하는 구현체
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	//jackson은 json을 직렬화와 역직렬화하기 위해 필요한 라이브러리
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")


}

tasks.withType<Test> {
	useJUnitPlatform()
}
