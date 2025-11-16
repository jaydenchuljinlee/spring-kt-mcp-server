package com.ai.mcp.news.mcp

import com.ai.mcp.news.domain.News
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
        name = "getKoreanEconomicNews",
        description = "국내 주요 경제 뉴스(연합뉴스, 한국경제, 매일경제)를 가져옵니다."
    )
    fun getKoreanEconomicNews(
        @McpToolParam(description = "소스별 뉴스 개수", required = false)
        count: Int = 5
    ): List<News> {
        return runBlocking {
            fetcher.fetchAll(count)
        }
    }
}