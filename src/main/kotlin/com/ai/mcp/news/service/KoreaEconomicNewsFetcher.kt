package com.ai.mcp.news.service

import com.ai.mcp.comon.config.properties.NewsProperties
import com.ai.mcp.comon.utils.RssParser
import com.ai.mcp.news.domain.News
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class KoreaEconomicNewsFetcher(
    private val rssParser: RssParser,
    private val properties: NewsProperties
) {
    private val log = LoggerFactory.getLogger(javaClass)

    suspend fun fetchAll(limitPerSource: Int = 5): List<News> = coroutineScope {
        val activeSources = properties.sources.values.filter { it.enabled }

        val results = activeSources.map { src ->
            async(Dispatchers.IO) {
                runCatching {
                    log.info("📡 Fetching RSS from ${src.name} (${src.url})")
                    rssParser.parse(src.name, src.url, limitPerSource)
                        .map { item ->
                            News(
                                source = item.source,
                                title = item.title,
                                summary = item.description,
                                link = item.link,
                                publishedAt = item.publishedAt
                            )
                        }
                }.getOrElse {
                    log.warn("❌ Failed to fetch from ${src.name}: ${it.message}")
                    emptyList()
                }
            }
        }

        results.awaitAll()
            .flatten()
            .sortedByDescending { it.publishedAt }
            .also { log.info("✅ Aggregated ${it.size} articles from ${activeSources.size} sources.") }
    }

}