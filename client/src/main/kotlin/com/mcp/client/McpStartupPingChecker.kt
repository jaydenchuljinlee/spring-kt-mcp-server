package com.mcp.client

import io.github.oshai.kotlinlogging.KotlinLogging
import io.modelcontextprotocol.client.McpSyncClient
import io.modelcontextprotocol.spec.McpSchema
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

@Component
class McpStartupPingChecker(
    private val clients: List<McpSyncClient>
) {

    @EventListener(ApplicationReadyEvent::class)
    fun onReady() {
        try {
            val client = clients.first()

            val request = McpSchema.CallToolRequest.builder()
                .name("ping")
                .arguments(emptyMap()) // ping은 인수가 없음
                .build()

            val result = client.callTool(request)

            log.info { "${"🚀 MCP Ping Success: {}"} $result" }
        } catch (ex: Exception) {
            log.error(ex) { "❌ MCP Ping Failed" }
        }
    }
}