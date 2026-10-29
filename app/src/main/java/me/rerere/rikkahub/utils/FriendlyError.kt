package me.rerere.rikkahub.utils

import androidx.annotation.StringRes
import me.rerere.ai.provider.ProviderSetting
import me.rerere.rikkahub.R

/** Coarse, user-facing classification of provider/API failures. */
enum class FriendlyErrorKind { INVALID_KEY, QUOTA, NETWORK, MODEL_NOT_FOUND }

private val QUOTA_RE = Regex("""\b429\b|rate.?limit|quota|insufficient_quota|resource_exhausted|too many requests|insufficient (balance|credit)""")
private val KEY_RE = Regex("""\b40[13]\b|unauthori[sz]ed|invalid.{0,12}key|incorrect api key|api key not valid|authentication|permission_denied|forbidden""")
private val MODEL_RE = Regex("""\b404\b|model.{0,40}(not found|does not exist|not exist|not supported)|model_not_found""")
private val NET_RE = Regex("""unable to resolve host|timeout|timed out|connection (reset|refused|closed)|failed to connect|network is unreachable|unexpected end of stream""")

fun Throwable.friendlyErrorKind(): FriendlyErrorKind? {
    val sb = StringBuilder()
    var t: Throwable? = this
    var depth = 0
    while (t != null && depth < 8) {
        if (t is java.net.UnknownHostException || t is java.net.ConnectException ||
            t is java.net.SocketTimeoutException || t is javax.net.ssl.SSLException
        ) return FriendlyErrorKind.NETWORK
        sb.append(t.javaClass.name).append(' ').append(t.message ?: "").append('\n')
        t = t.cause?.takeIf { it !== t }
        depth++
    }
    val s = sb.toString().lowercase()
    return when {
        QUOTA_RE.containsMatchIn(s) -> FriendlyErrorKind.QUOTA
        KEY_RE.containsMatchIn(s) -> FriendlyErrorKind.INVALID_KEY
        MODEL_RE.containsMatchIn(s) -> FriendlyErrorKind.MODEL_NOT_FOUND
        NET_RE.containsMatchIn(s) -> FriendlyErrorKind.NETWORK
        else -> null
    }
}

@StringRes
fun FriendlyErrorKind.messageRes(): Int = when (this) {
    FriendlyErrorKind.INVALID_KEY -> R.string.friendly_error_invalid_key
    FriendlyErrorKind.QUOTA -> R.string.friendly_error_quota
    FriendlyErrorKind.NETWORK -> R.string.friendly_error_network
    FriendlyErrorKind.MODEL_NOT_FOUND -> R.string.friendly_error_model_not_found
}

/** Returns a copy of this provider with [key] set as API key (no-op for account-based providers). */
fun ProviderSetting.withApiKey(key: String): ProviderSetting = when (this) {
    is ProviderSetting.OpenAI -> copy(apiKey = key, enabled = true)
    is ProviderSetting.Google -> copy(apiKey = key, enabled = true)
    is ProviderSetting.Claude -> copy(apiKey = key, enabled = true)
    else -> this
}