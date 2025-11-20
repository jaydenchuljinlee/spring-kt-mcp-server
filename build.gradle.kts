plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.3.0"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.ai"
version = "0.0.1-SNAPSHOT"
description = "MCP 서버"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
	// implementation("org.springframework.boot:spring-boot-starter-actuator")
	// implementation("org.springframework.boot:spring-boot-starter-web")
	// implementation("org.springframework.boot:spring-boot-starter-webflux")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	// implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")


	// ✅ Spring AI 1.1.0 (MCP 서버 지원)
	implementation(platform("org.springframework.ai:spring-ai-bom:1.1.0"))

	// ✅ OpenAI 모델 (기존 그대로 유지)
	// implementation("org.springframework.ai:spring-ai-starter-model-openai")

	// ✅ Memory Repository (기존 그대로)
	// implementation("org.springframework.ai:spring-ai-starter-model-chat-memory-repository-jdbc")

	// ✅ MCP 서버(WebFlux)
	implementation("org.springframework.ai:spring-ai-starter-mcp-server-webmvc")

	// ✅ 코루틴 / Flow
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")



	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}


tasks.withType<Test> {
	useJUnitPlatform()
}
