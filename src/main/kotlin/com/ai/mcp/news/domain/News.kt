package com.ai.mcp.news.domain

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.Instant

data class News(
    val source: String,
    val title: String,
    val summary: String,
    val link: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    val publishedAt: Instant
)

