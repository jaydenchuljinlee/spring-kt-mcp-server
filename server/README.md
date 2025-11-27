# MCP 서버

Spring AI 1.1.0의 MCP(Multi-Content Prompt) 서버 기능을 활용한 프로젝트입니다.

## 1. 기술 스택

-   **언어**: Kotlin 1.9.25
-   **프레임워크**: Spring Boot 3.3.0
-   **Java 버전**: Java 21
-   **핵심 라이브러리**:
    -   Spring AI 1.1.0
    -   Kotlin Coroutines

## 2. MCP 관련 라이브러리

-   `org.springframework.ai:spring-ai-starter-mcp-server-webmvc`
    -   Spring Boot WebMVC 환경에서 MCP 서버를 구축하기 위한 스타터 라이브러리입니다.
    -   `@McpTool` 어노테이션을 스캔하여 자동으로 REST API 엔드포인트를 생성합니다.

## 3. 제공되는 MCP Tool 목록

### 1) `ping`

-   **설명**: MCP 서버의 연결 상태를 확인합니다.
-   **파라미터**: 없음
-   **응답**: `"pong from MCP server!"`

### 2) `getNews`

-   **설명**: 카테고리, 뉴스사, 기간을 기준으로 국내 주요 뉴스를 가져옵니다.
-   **파라미터**:
    -   `category` (String, Optional, 기본값: `ECONOMY`): 뉴스 카테고리 (`ECONOMY`, `POLITICS`, `ALL`)
    -   `sources` (List<String>, Optional): 뉴스 출처 (`YONHAP`, `HANKYUNG`, `MK`)
    -   `period` (String, Optional, 기본값: `6h`): 조회 기간 (예: `1h`, `6h`, `1d`)
    -   `limit` (Int, Optional, 기본값: `10`): 조회할 뉴스 개수

## 4. MCP Tool 등록 방법

1.  `@Component` 어노테이션을 사용하여 Spring Bean으로 클래스를 등록합니다.
2.  Tool로 노출할 메소드를 생성하고 `@McpTool` 어노테이션을 붙입니다.
    -   `name`: Tool의 이름 (API 경로로 사용됨)
    -   `description`: Tool의 기능 설명
3.  메소드의 파라미터에는 `@McpToolParam` 어노테이션을 사용하여 설명을 추가할 수 있습니다.

**예시: `PingTool.kt`**

```kotlin
package com.ai.mcp.comon.mcp

import org.springaicommunity.mcp.annotation.McpTool
import org.springframework.stereotype.Component

@Component
class PingTool {
    @McpTool(name = "ping", description = "Check MCP connectivity")
    fun ping(): String {
        return "pong from MCP server!"
    }
}
```

## 5. MCP Tool 호출 방법

`spring-ai-starter-mcp-server-webmvc` 라이브러리가 Tool을 자동으로 RESTful API로 노출합니다. `POST` 메소드를 사용하여 호출할 수 있습니다.

-   **URL 형식**: `http://<host>:<port>/mcp/{toolName}`
-   **HTTP Method**: `POST`
-   **Content-Type**: `application/json`
-   **Request Body**: Tool의 파라미터를 JSON 형식으로 전달합니다.

### 호출 예시 1: `ping`

```shell
curl -X POST http://localhost:8080/mcp/ping
```

### 호출 예시 2: `getNews`

```shell
curl -X POST http://localhost:8080/mcp/getNews \
-H "Content-Type: application/json" \
-d '{
      "category": "ECONOMY",
      "sources": ["HANKYUNG", "MK"],
      "period": "1d",
      "limit": 5
    }'
```
