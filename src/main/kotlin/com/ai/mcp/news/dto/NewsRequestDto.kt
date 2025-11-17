package com.ai.mcp.news.dto

data class NewsRequestDto(
    val category: String? = null,
    val sources: List<String>? = null,
    val period: String? = "1h",
    val limit: Int = 10
)
