package com.ai.mcp.news.service

import com.ai.mcp.comon.config.properties.NewsProperties
import com.ai.mcp.comon.utils.RssParser
import com.ai.mcp.comon.utils.RssUrlResolver
import com.ai.mcp.news.domain.News
import com.ai.mcp.news.domain.enums.NewsSource
import com.ai.mcp.news.dto.NewsRequestDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class KoreaEconomicNewsFetcher(
    private val rssParser: RssParser,
    private val properties: NewsProperties,
    private val rssUrlResolver: RssUrlResolver
) {
    private val log = LoggerFactory.getLogger(javaClass)

    suspend fun fetchAll(request: NewsRequestDto): List<News> = coroutineScope {
        val activeSources = (request.sources?.mapNotNull { NewsSource.from(it) }
            ?: NewsSource.entries)
            .filter { properties.sources[it.name.lowercase()]?.enabled == true }

        val category = request.category ?: "economy"
        val cutoff = Instant.now().minus(parsePeriod(request.period ?: "1h"))

        val results = activeSources.map { src ->
            async(Dispatchers.IO) {
                runCatching {
                    val url = rssUrlResolver.resolve(src, category)
                        ?: return@runCatching emptyList<News>()

                    log.info("📡 Fetching RSS from ${src.displayName} ($url)")

                    rssParser.parse(src.displayName, url, request.limit)
                        .filter { it.publishedAt.isAfter(cutoff) }
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
                    log.warn("❌ Failed to fetch from ${src.displayName}: ${it.message}")
                    emptyList()
                }
            }
        }

        results.awaitAll()
            .flatten()
            .sortedByDescending { it.publishedAt }
            .also {
                log.info("✅ Aggregated ${it.size} articles from ${activeSources.size} sources (category=$category)")
            }
    }

    private fun parsePeriod(period: String): Duration = when {
        period.endsWith("h") -> Duration.ofHours(period.dropLast(1).toLong())
        period.endsWith("d") -> Duration.ofDays(period.dropLast(1).toLong())
        period.endsWith("m") -> Duration.ofMinutes(period.dropLast(1).toLong())
        else -> Duration.ofHours(1)
    }





}