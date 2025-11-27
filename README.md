# 🚀 Spring MCP Architecture (Server & Client)

이 프로젝트는 Spring AI 기반 **MCP Server**와 **MCP Client**가
JSON-RPC 기반 프로토콜을 통해 상호 통신하도록 구성되어 있습니다.

---

## 📦 프로젝트 구조

```
root 
├── server # MCP Server (Tool 제공) 
└── client # MCP Client (Tool 소비자) 
```
---

## 🧩 MCP Server

Spring AI의 MCP Server Starter를 사용하여 서버를 구성합니다.

서버는 다음 역할을 가집니다:

- MCP Tool을 외부에 노출
- JSON-RPC 기반의 `/mcp` SSE 엔드포인트 제공
- 클라이언트에서 Tool 목록 조회 및 Tool 호출 가능

서버는 반드시 **MCP Client보다 먼저 실행되어야 합니다.**

---

## 🧩 MCP Client

Client는 Spring AI MCP Client Starter 기반으로 구동되며:

- 애플리케이션 기동 시 MCP Server에 연결을 시도
- `initialize` 핸드셰이크 수행
- 서버의 Tool 목록을 자동으로 조회
- Tool을 직접 호출할 수 있는 API 제공

클라이언트는 내부적으로 다음을 수행합니다:

1) 서버의 `/mcp` SSE 엔드포인트와 연결  
2) 초기화(`initialize`) 요청  
3) 서버 Tool 목록(`listTools`) 조회  
4) 필요 시 `callTool`로 원격 Tool 실행  

---

## 🔗 서버 → 클라이언트 실행 순서

1) **MCP Server 실행**
   - `/mcp` SSE endpoint가 올라감
   - MCP Tools가 로드됨

2) **MCP Client 실행**
   - 서버로 초기화 메시지(`initialize`) 전송
   - Tool 목록 자동 조회
   - 필요 시 Tool 호출 가능

---

## 📡 MCP 통신 방식

본 프로젝트는 **SSE(Streamable) 기반 MCP Server**를 사용합니다:

- 서버는 `/mcp` 단일 엔드포인트만 사용
- 클라이언트는 SSE 채널을 유지하며 요청/응답을 주고받음
- 별도 sessionId를 필요로 하지 않아 MCP 스펙과 완전히 호환됨

---

## 📝 주요 기능

- 서버 Tool 등록  
- 클라이언트 Tool 목록 조회  
- 클라이언트 → 서버 Tool 호출(JSON-RPC)  
- SSE 기반 양방향 스트리밍  

---

## ▶️ 실행 방법

### 1) MCP Server 실행
서버 실행 후 다음과 같이 응답되면 정상:

POST /mcp (initialize)
→ MCP capabilities 및 serverInfo 응답

### 2) MCP Client 실행
클라이언트가 자동으로 서버 초기화 및 Tool 목록 조회 수행.
