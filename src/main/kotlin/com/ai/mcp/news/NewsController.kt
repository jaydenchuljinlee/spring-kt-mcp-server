package com.ai.mcp.news

import com.ai.mcp.news.domain.News
import com.ai.mcp.news.service.KoreaEconomicNewsFetcher
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/news")
class NewsController(
    private val fetcher: KoreaEconomicNewsFetcher
) {

    @GetMapping("/economy")
    suspend fun getEconomyNews(): List<News> = fetcher.fetchAll(5)
}