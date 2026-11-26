package me.rerere.rikkahub.data.remote

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import me.rerere.common.http.await
import me.rerere.rikkahub.data.model.Sponsor
import me.rerere.rikkahub.utils.AppLinks
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Sponsors of rikkahub-ultra only. The list lives in `sponsors.json` of our GitHub repository
 * ([AppLinks.SPONSORS_JSON]); the copy bundled at build time is used when the network fails.
 * Upstream RikkaHub sponsors are intentionally not shown.
 */
class SponsorAPI(
    private val context: Context,
    private val client: OkHttpClient,
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getSponsors(): List<Sponsor> = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.newCall(Request.Builder().url(AppLinks.SPONSORS_JSON).get().build()).await()
            if (!response.isSuccessful) error("HTTP ${response.code}")
            json.decodeFromString<List<Sponsor>>(response.body.string())
        }.getOrElse { bundled() }
    }

    private fun bundled(): List<Sponsor> = runCatching {
        context.assets.open("sponsors.json").bufferedReader().use { json.decodeFromString<List<Sponsor>>(it.readText()) }
    }.getOrDefault(emptyList())
}