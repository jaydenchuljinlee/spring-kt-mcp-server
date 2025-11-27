# MCP 클라이언트

## 1. 기술 스택

- Kotlin (JVM)
- Spring Boot 3.3.0
- Java 21
- Spring AI 1.1.0

## 2. MCP 관련 라이브러리

`build.gradle.kts` 파일에 정의된 주요 MCP 관련 라이브러리는 다음과 같습니다.

```kotlin
dependencies {
    // ...
    implementation("org.springframework.ai:spring-ai-starter-mcp-client")
    // ...
}
```

## 3. MCP 서버 연결 설정

`src/main/resources/application.yaml` 파일에서 MCP 서버 연결을 설정할 수 있습니다.

### 3.1. 설정 방법

```yaml
spring:
  ai:
    mcp:
      client:
        enabled: true
        type: SYNC
        sse:
          connections:
            newsServer: # 연결할 서버의 별칭
              url: http://localhost:8080/mcp # MCP 서버의 URL
```

- `spring.ai.mcp.client.enabled`: `true`로 설정하여 MCP 클라이언트를 활성화합니다.
- `spring.ai.mcp.client.type`: 클라이언트 타입을 설정합니다. (예: `SYNC`)
- `spring.ai.mcp.client.sse.connections`: 연결할 MCP 서버 목록을 정의합니다.
    - `newsServer`: 각 서버를 식별하기 위한 고유한 이름(별칭)입니다.
    - `url`: 해당 MCP 서버의 엔드포인트 주소입니다.

### 3.2. 추후 MCP 서버 추가 시

새로운 MCP 서버를 추가하려면 `connections` 아래에 새로운 항목을 추가하면 됩니다.

```yaml
spring:
  ai:
    mcp:
      client:
        sse:
          connections:
            newsServer:
              url: http://localhost:8080/mcp
            anotherServer: # 새로운 서버 추가
              url: http://another-mcp-server.com/mcp
```

## 4. Tool 체크 (Ping)

애플리케이션 시작 시 등록된 MCP 서버가 정상적으로 동작하는지 확인하기 위해 `ping` Tool을 호출합니다.

`McpStartupPingChecker.kt` 파일에서 이 로직을 확인할 수 있습니다.

```kotlin
@Component
class McpStartupPingChecker(
    private val clients: List<McpSyncClient>
) {
    @EventListener(ApplicationReadyEvent::class)
    fun onReady() {
        try {
            val client = clients.first() // 첫 번째 MCP 클라이언트를 가져옵니다.

            // 'ping'이라는 이름의 Tool을 호출합니다.
            val request = McpSchema.CallToolRequest.builder()
                .name("ping")
                .arguments(emptyMap())
                .build()

            val result = client.callTool(request)
            log.info { "🚀 MCP Ping Success: {}" $result }
        } catch (ex: Exception) {
            log.error(ex) { "❌ MCP Ping Failed" }
        }
    }
}
```

- `ApplicationReadyEvent` 리스너를 통해 애플리케이션이 준비되면 `onReady` 메소드가 실행됩니다.
- 주입된 `McpSyncClient` 목록에서 첫 번째 클라이언트를 사용하여 `ping`이라는 이름의 Tool을 호출합니다.
- 호출 성공 시 "MCP Ping Success" 로그가, 실패 시 "MCP Ping Failed" 로그가 출력됩니다.
- **참고:** 이 기능이 정상적으로 동작하려면 연결된 MCP 서버에 `ping`이라는 이름의 인수가 없는 Tool이 구현되어 있어야 합니다.
