package me.rerere.rikkahub.ui.components.ai

import android.content.Context
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Quality scores for well-known model families, bundled in assets/model_rankings.json.
 * Model ids coming from different providers are normalized so that e.g.
 * "openai/gpt-4o-2024-08-06", "gpt-4o" and "GPT-4o-latest" map to the same entry.
 */
class ModelQualityRanking(
    val source: String,
    val date: String,
    scores: Map<String, Double>,
    aliases: Map<String, String>,
) {
    private val scores: Map<String, Double> = scores.mapKeys { normalizeModelId(it.key) }
    private val aliases: Map<String, String> =
        aliases.entries.associate { normalizeModelId(it.key) to normalizeModelId(it.value) }
    private val keysByLength = this.scores.keys.sortedByDescending { it.length }

    /** Canonical key used for grouping the same model across providers. */
    fun canonicalId(modelId: String): String {
        val n = normalizeModelId(modelId)
        return aliases[n] ?: n
    }

    fun score(modelId: String): Double? {
        val id = canonicalId(modelId)
        scores[id]?.let { return it }
        // fall back to the longest known family prefix, e.g. "gpt-4o-search-preview" -> "gpt-4o"
        val key = keysByLength.firstOrNull { id.startsWith("$it-") } ?: return null
        return scores[key]
    }

    companion object {
        val EMPTY = ModelQualityRanking("", "", emptyMap(), emptyMap())

        @Volatile
        private var cached: ModelQualityRanking? = null

        fun load(context: Context): ModelQualityRanking {
            cached?.let { return it }
            val result = runCatching {
                val text = context.assets.open("model_rankings.json").bufferedReader().use { it.readText() }
                parse(text)
            }.getOrElse {
                it.printStackTrace()
                EMPTY
            }
            cached = result
            return result
        }

        fun parse(text: String): ModelQualityRanking {
            val root = Json.parseToJsonElement(text).jsonObject
            val scores = (root["scores"] as? JsonObject).orEmpty().mapNotNull { (k, v) ->
                v.jsonPrimitive.doubleOrNull?.let { k to it }
            }.toMap()
            val aliases = (root["aliases"] as? JsonObject).orEmpty().mapNotNull { (k, v) ->
                v.jsonPrimitive.contentOrNull?.let { k to it }
            }.toMap()
            return ModelQualityRanking(
                source = root["source"]?.jsonPrimitive?.contentOrNull.orEmpty(),
                date = root["date"]?.jsonPrimitive?.contentOrNull.orEmpty(),
                scores = scores,
                aliases = aliases,
            )
        }
    }
}

private val DROPPED_TAGS = setOf("free", "beta", "extended", "thinking", "online", "nitro", "floor", "exacto", "latest")
private val DATE_SUFFIXES = listOf(
    Regex("""-20\d{2}-?\d{2}-?\d{2}$"""),   // -2024-08-06 / -20241022
    Regex("""-\d{2}-\d{2}$"""),             // -05-20 (gemini previews)
    Regex("""-(0[1-9]|1[0-2])\d{2}$"""),     // -0613 / -2407 style mmdd/yymm
    Regex("""-(2[3-9])(0[1-9]|1[0-2])$"""),  // -2411 (yymm)
    Regex("""-latest$"""),
)

/**
 * Normalize a provider model id:
 * lowercase, strip provider prefix ("openai/"), strip ":free"-style tags,
 * strip date / "-latest" suffixes and unify "4.5" vs "4-5".
 */
fun normalizeModelId(modelId: String): String {
    var id = modelId.trim().lowercase()
    id = id.substringAfterLast('/')
    // drop known ":tag" suffixes (OpenRouter style)
    while (true) {
        val idx = id.lastIndexOf(':')
        if (idx < 0) break
        val tag = id.substring(idx + 1)
        if (tag in DROPPED_TAGS) id = id.substring(0, idx) else break
    }
    id = id.replace('_', '-').replace(' ', '-')
    var changed = true
    while (changed) {
        changed = false
        for (r in DATE_SUFFIXES) {
            val n = r.replace(id, "")
            if (n != id && n.isNotEmpty()) {
                id = n; changed = true
            }
        }
    }
    id = Regex("""(\d)\.(\d)""").replace(id, "$1-$2")
    return id
}

/** True for models a provider marks as free (OpenRouter ":free" suffix). */
fun isFreeModelId(modelId: String): Boolean {
    val id = modelId.trim().lowercase()
    return id.endsWith(":free") || id.contains(":free:")
}
