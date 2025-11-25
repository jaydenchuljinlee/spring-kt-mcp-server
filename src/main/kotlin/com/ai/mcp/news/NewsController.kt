package com.ai.mcp.news

import com.ai.mcp.news.domain.News
import com.ai.mcp.news.dto.NewsRequestDto
import com.ai.mcp.news.service.KoreaEconomicNewsFetcher
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/news")
class NewsController(
    private val fetcher: KoreaEconomicNewsFetcher
) {

    @GetMapping()
    suspend fun getEconomyNews(
        @RequestParam category: String?,
        @RequestParam sources: List<String>?,
        @RequestParam period: String?,
        @RequestParam limit: Int?
    ): List<News> {
        val request = NewsRequestDto(category, sources, period, limit ?: 10)
        return fetcher.fetchAll(request)
    }
}