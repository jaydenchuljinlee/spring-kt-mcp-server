package com.ai.mcp.news.domain.enums

enum class NewsSource(val displayName: String) {
    YONHAP("연합뉴스"),
    HANKYUNG("한국경제"),
    MK("매일경제");

    companion object {
        fun from(input: String): NewsSource? =
            entries.firstOrNull { it.name.equals(input, ignoreCase = true) || it.displayName == input }
    }
}
