package com.ai.mcp.comon.utils

import org.springframework.stereotype.Component
import org.w3c.dom.Element
import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URI
import java.time.Instant
import java.time.LocalDateTime
import javax.xml.parsers.DocumentBuilderFactory
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Component
class RssParser {
    private val factory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance().apply {
        isNamespaceAware = false
        isValidating = false
        // 외부 DTD/엔티티 로드 방지 (보안/성능)
        try {
            setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
            setFeature("http://xml.org/sax/features/external-general-entities", false)
            setFeature("http://xml.org/sax/features/external-parameter-entities", false)
        } catch (_: Exception) { /* 일부 구현체는 미지원일 수 있음 */ }
    }

    fun parse(source: String, feedUrl: String, limit: Int = 10): List<RssItem> =
        runCatching {
            val url = URI(feedUrl).toURL()
            val conn = (url.openConnection() as HttpURLConnection).apply {
                instanceFollowRedirects = true
                connectTimeout = 5000
                readTimeout = 5000
                useCaches = false
                // ✅ 브라우저 흉내 헤더
                setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; RSSFetcher/1.0)")
                setRequestProperty("Accept", "application/rss+xml, application/xml, text/xml;q=0.9, */*;q=0.8")
                setRequestProperty("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
            }

            val code = conn.responseCode
            if (code !in 200..299) return emptyList() // 비정상 응답

            val contentType = (conn.contentType ?: "").lowercase()
            // ✅ RSS/XML 이 아니면 XML 파싱 시도하지 않음 (HTML 차단)
            if (!contentType.contains("xml") && !contentType.contains("rss")) {
                conn.inputStream.close()
                return emptyList()
            }

            BufferedInputStream(conn.inputStream).use { input ->
                val document = factory.newDocumentBuilder().parse(input)
                val items = document.getElementsByTagName("item")

                (0 until items.length).mapNotNull { idx ->
                    val element = items.item(idx) as? Element ?: return@mapNotNull null

                    val title = element.getTagValue("title")?.trim() ?: return@mapNotNull null
                    val desc = element.getTagValue("description")?.cleanHtml()?.trim().orEmpty()
                    val link = element.getTagValue("link")?.trim().orEmpty()
                    val pubDate = element.getTagValue("pubDate")

                    val publishedAt = pubDate?.toInstantOrNow() ?: Instant.now()

                    RssItem(
                        source = source,
                        title = title,
                        description = desc.take(200),
                        link = link,
                        publishedAt = publishedAt
                    )
                }.take(limit)
            }
        }.getOrElse {
            // TODO: 여기서 로깅/모니터링 연결 (원인 파악)
            emptyList()
        }

    private fun Element.getTagValue(tag: String): String? =
        getElementsByTagName(tag).item(0)?.textContent

    private fun String.cleanHtml(): String =
        replace("<.*?>".toRegex(), "")

    private fun String.toInstantOrNow(): Instant =
        runCatching {
            val zdt = ZonedDateTime.parse(this, DateTimeFormatter.RFC_1123_DATE_TIME)
            // 한국 시간으로 변환
            zdt.withZoneSameInstant(java.time.ZoneId.of("Asia/Seoul")).toInstant()
        }.getOrElse { Instant.now() }



    data class RssItem(
        val source: String,
        val title: String,
        val description: String,
        val link: String,
        val publishedAt: Instant
    )
}