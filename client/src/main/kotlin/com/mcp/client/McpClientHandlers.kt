package com.mcp.client

import io.modelcontextprotocol.spec.McpSchema.LoggingMessageNotification
import org.springaicommunity.mcp.annotation.McpLogging
import org.springframework.stereotype.Component


@Component
class McpClientHandlers {
    @McpLogging(clients = ["news-server"])
    fun handleLoggingMessage(notification: LoggingMessageNotification) {
        println(
            "Received log: " + notification.level() +
                    " - " + notification.data()
        )
    }
}