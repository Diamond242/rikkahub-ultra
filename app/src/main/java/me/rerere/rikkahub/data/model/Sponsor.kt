package me.rerere.rikkahub.data.model

import kotlinx.serialization.Serializable

/**
 * One entry of the rikkahub-ultra sponsor list (`sponsors.json` in the repository root).
 *
 * Example: `[{"userName": "Ali", "avatar": "https://...", "amount": "10 USDT", "url": "https://..."}]`
 */
@Serializable
data class Sponsor(
    val userName: String,
    val avatar: String = "",
    val amount: String = "",
    val url: String = "",
)