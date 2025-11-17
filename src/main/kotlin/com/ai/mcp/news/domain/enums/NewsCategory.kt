package com.ai.mcp.news.domain.enums

enum class NewsCategory(val key: String, val displayName: String) {
    ECONOMY("economy", "경제"),
    POLITICS("politics", "정치"),
    ALL("all", "전체");

    companion object {
        fun from(input: String?): NewsCategory =
            entries.firstOrNull {
                it.name.equals(input, ignoreCase = true)
                        || it.key.equals(input, ignoreCase = true)
                        || it.displayName == input
            } ?: ECONOMY
    }
}
