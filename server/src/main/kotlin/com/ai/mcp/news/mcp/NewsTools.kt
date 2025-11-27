package com.ai.mcp.news.mcp

import com.ai.mcp.news.domain.News
import com.ai.mcp.news.domain.enums.NewsCategory
import com.ai.mcp.news.domain.enums.NewsSource
import com.ai.mcp.news.dto.NewsRequestDto
import com.ai.mcp.news.service.KoreaEconomicNewsFetcher
import kotlinx.coroutines.runBlocking
import org.springaicommunity.mcp.annotation.McpTool
import org.springaicommunity.mcp.annotation.McpToolParam
import org.springframework.stereotype.Component

@Component
class NewsTools(
    private val fetcher: KoreaEconomicNewsFetcher
) {
    @McpTool(
        name = "getNews",
        description = "카테고리, 뉴스사, 기간을 기준으로 국내 주요 뉴스를 가져옵니다."
    )
    fun getNews(
        @McpToolParam(description = "뉴스 카테고리 (ECONOMY=경제, POLITICS=정치, ALL=전체)")
        category: NewsCategory? = NewsCategory.ECONOMY,

        @McpToolParam(description = "뉴스 출처 (YONHAP=연합뉴스, HANKYUNG=한국경제, MK=매일경제)")
        sources: List<NewsSource>? = null,

        @McpToolParam(description = "조회 기간 (예: 1h=1시간, 6h=6시간, 1d=1일)")
        period: String? = "6h",

        @McpToolParam(description = "뉴스 개수 제한") limit: Int = 10
    ): List<News> = runBlocking {
        val req = NewsRequestDto(
            category = category?.key,
            sources = sources?.map { it.name.lowercase() },
            period = period,
            limit = limit
        )
        fetcher.fetchAll(req)
    }
}