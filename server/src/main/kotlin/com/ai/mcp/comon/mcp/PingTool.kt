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