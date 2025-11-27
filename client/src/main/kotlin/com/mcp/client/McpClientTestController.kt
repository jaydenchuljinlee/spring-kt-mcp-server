package com.mcp.client

import io.modelcontextprotocol.client.McpSyncClient
import io.modelcontextprotocol.spec.McpSchema
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class McpClientTestController(
    private val mcpSyncClients: List<McpSyncClient>
) {

    @GetMapping("/tools")
    fun listTools(): List<McpSchema.ListToolsResult> {
        return mcpSyncClients.map { it.listTools() }
    }
}