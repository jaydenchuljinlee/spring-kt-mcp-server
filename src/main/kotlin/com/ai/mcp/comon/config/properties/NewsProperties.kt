package com.ai.mcp.comon.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "news.rss")
data class NewsProperties(
    var sources: Map<String, RssSource> = emptyMap()
) {
    data class RssSource(
        var name: String = "",
        var url: String = "",
        var enabled: Boolean = true,
        var categories: Map<String, String> = emptyMap()
    )
}