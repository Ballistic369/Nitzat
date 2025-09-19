package com.nitzatGPT.nitzatsearch

import com.nitzatGPT.nitzatsearch.model.Product
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.net.URLEncoder

class ProductRepository {
    private val client = OkHttpClient()

    fun searchProducts(query: String): List<Product> {
        if (query.isBlank()) return emptyList()
        val q = URLEncoder.encode(query, "UTF-8")
        val url = "https://www.nizat.com/Search?sSearch=$q"

        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "Mozilla/5.0")
            .build()

        client.newCall(request).execute().use { resp ->
            val body = resp.body?.string() ?: return emptyList()
            val doc = Jsoup.parse(body)

            val results = mutableListOf<Product>()

            val items = doc.select("div.product, div.product-item, div.product-list-item, article, div.product-card")

            if (items.isEmpty()) {
                // try alternative common container
                val alt = doc.select(".product-tile, .product-list .item")
                parseItems(alt, results)
            } else {
                parseItems(items, results)
            }

            return results
        }
    }

    fun fetchProductDetails(productUrl: String): Product? {
        val url = if (productUrl.startsWith("http")) productUrl else "https://www.nizat.com$productUrl"
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "Mozilla/5.0")
            .build()
        client.newCall(request).execute().use { resp ->
            val body = resp.body?.string() ?: return null
            val doc = Jsoup.parse(body)

            val name = doc.selectFirst("h1")?.text()?.trim() ?: return null

            var imageUrl = doc.selectFirst("div.main-product-image img")?.attr("src")
            if (imageUrl != null && imageUrl.startsWith("/")) imageUrl = "https://www.nizat.com$imageUrl"

            val offerPrice = doc.selectFirst("div.offer-price, span.price-offer, span.price")?.text()?.trim()
            val originalPrice = doc.selectFirst("div.original-price, span.price-original, .price-old, .compare-price")?.text()?.trim()

            val descriptionElem = doc.selectFirst("div.product-details, .product-info, #product-description")
            val description = descriptionElem?.text()?.trim()

            return Product(
                name = name,
                imageUrl = imageUrl,
                price = offerPrice ?: originalPrice ?: "",
                discountPrice = if (!originalPrice.isNullOrBlank() && originalPrice != offerPrice) originalPrice else null,
                description = description,
                link = url
            )
        }
    }

    private fun parseItems(items: org.jsoup.select.Elements, results: MutableList<Product>) {
        for (item in items) {
            val name = item.selectFirst(".product-name, .title, h2, .name, .product-title")?.text() ?: "<unknown>"
            var image = item.selectFirst("img")?.attr("src")
            if (image != null && image.startsWith("/")) image = "https://www.nizat.com$image"

            val priceText = item.selectFirst(".price, .product-price, .pricetag, .product-price .price, .price-offer")?.text()
            val discountText = item.selectFirst(".discount, .old-price, .compare-price, .price-old")?.text()

            val link = item.selectFirst("a")?.attr("href") ?: ""
            val linkFull = if (link.startsWith("http")) link else "https://www.nizat.com$link"

            results.add(
                Product(
                    name = name,
                    imageUrl = image,
                    price = priceText ?: "",
                    discountPrice = discountText?.takeIf { it.isNotBlank() },
                    description = null,
                    link = linkFull
                )
            )
        }
    }
}
