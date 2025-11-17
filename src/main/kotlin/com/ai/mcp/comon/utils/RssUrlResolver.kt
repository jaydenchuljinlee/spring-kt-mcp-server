package com.ai.mcp.comon.utils

import com.ai.mcp.comon.config.properties.NewsProperties
import com.ai.mcp.news.domain.enums.NewsSource
import org.springframework.stereotype.Component

@Component
class RssUrlResolver(
    private val props: NewsProperties
) {
    fun resolve(source: NewsSource, category: String?): String? {
        val config = props.sources[source.name.lowercase()] ?: return null
        if (!config.enabled) return null

        val categoryKey = category?.lowercase() ?: "economy"
        val baseUrl = config.url.trimEnd('/')
        val path = config.categories[categoryKey]?.trimStart('/')

        if (path.isNullOrBlank()) return null

        return "$baseUrl/$path"
    }
}