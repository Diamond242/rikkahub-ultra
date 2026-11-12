package me.rerere.rikkahub.data.datastore

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import kotlinx.serialization.Serializable
import me.rerere.ai.provider.ProviderSetting
import me.rerere.rikkahub.R
import kotlin.uuid.Uuid

/** Pricing tier of a provider's API access. */
@Serializable
enum class ProviderTier { FREE, TRIAL, FREE_QUOTA, PAID }

/** True when the provider can be used without paying (free tier, trial credit or free quota). */
val ProviderTier?.isFreeUsable: Boolean get() = this != null && this != ProviderTier.PAID

/** Short label resource for the tier badge. */
val ProviderTier.labelRes: Int
    get() = when (this) {
        ProviderTier.FREE -> R.string.provider_tier_free
        ProviderTier.TRIAL -> R.string.provider_tier_trial
        ProviderTier.FREE_QUOTA -> R.string.provider_tier_free_quota
        ProviderTier.PAID -> R.string.provider_tier_paid
    }

/** OpenRouter-style free model variants (model id ending in ":free"). */
val me.rerere.ai.provider.Model.isFreeVariant: Boolean get() = modelId.endsWith(":free")

/**
 * Static metadata for built-in providers (pricing tier, key requirement, reachability from Iran).
 * Kept outside [ProviderSetting] so stored settings never need migration; every field has a default.
 */
@Serializable
data class ProviderMeta(
    val tier: ProviderTier = ProviderTier.PAID,
    val noKeyRequired: Boolean = false,
    val worksInIranWithoutVpn: Boolean = false,
    val signupUrl: String? = null,
    /** Optional short note (string resource) describing the free quota / conditions. */
    val quotaNote: Int? = null,
)

val PROVIDER_META: Map<Uuid, ProviderMeta> = mapOf(
    Uuid.parse("97fb1325-5f4f-5981-8c0b-f57346d29665") to ProviderMeta(ProviderTier.FREE, noKeyRequired = false, worksInIranWithoutVpn = false, signupUrl = "https://console.groq.com/keys"),
    Uuid.parse("76c5f600-a6b7-5cb8-b6d9-0d3ef796db7a") to ProviderMeta(ProviderTier.FREE, noKeyRequired = false, worksInIranWithoutVpn = false, signupUrl = "https://build.nvidia.com/settings/api-keys"),
    Uuid.parse("7803fc83-af3d-5e09-8821-33cdcf1682f6") to ProviderMeta(ProviderTier.FREE, noKeyRequired = false, worksInIranWithoutVpn = false, signupUrl = "https://console.mistral.ai/api-keys"),
    Uuid.parse("ef29c2ad-ea6c-5bcb-9f22-0c66c9c7bd7e") to ProviderMeta(ProviderTier.FREE, noKeyRequired = false, worksInIranWithoutVpn = false, signupUrl = "https://dashboard.cohere.com/api-keys"),
    Uuid.parse("33fb36c5-d946-5a3c-ba42-103e96966f7e") to ProviderMeta(ProviderTier.FREE, noKeyRequired = false, worksInIranWithoutVpn = false, signupUrl = "https://z.ai/manage-apikey/apikey-list"),
    Uuid.parse("47814e33-8bb6-543c-99a7-785ed6f1b29a") to ProviderMeta(ProviderTier.FREE, noKeyRequired = false, worksInIranWithoutVpn = false, signupUrl = "https://cloud.siliconflow.com/account/ak"),
    Uuid.parse("65e59018-c4ab-5087-ad1e-6dc2a665c331") to ProviderMeta(ProviderTier.FREE, noKeyRequired = false, worksInIranWithoutVpn = false, signupUrl = "https://dash.llm7.io"),
    Uuid.parse("4efb9dd2-f0f0-5b71-bc12-ef3273de4e30") to ProviderMeta(ProviderTier.FREE, noKeyRequired = false, worksInIranWithoutVpn = false, signupUrl = "https://enter.pollinations.ai"),
    Uuid.parse("531dac70-e404-5164-be97-8e44c19c92ae") to ProviderMeta(ProviderTier.TRIAL, noKeyRequired = false, worksInIranWithoutVpn = false, signupUrl = "https://cloud.cerebras.ai"),
    Uuid.parse("f134f417-7c91-5091-8879-4131dfa50e2d") to ProviderMeta(ProviderTier.TRIAL, noKeyRequired = false, worksInIranWithoutVpn = false, signupUrl = "https://cloud.sambanova.ai/apis"),
    Uuid.parse("e7152bdc-7cca-5acf-ab4a-82ff25541f56") to ProviderMeta(ProviderTier.TRIAL, noKeyRequired = false, worksInIranWithoutVpn = false, signupUrl = "https://console.anthropic.com/settings/keys", quotaNote = R.string.provider_quota_anthropic),
    Uuid.parse("6b381ff2-51c6-5339-bde5-40f564be2d28") to ProviderMeta(ProviderTier.FREE, noKeyRequired = true, worksInIranWithoutVpn = true, signupUrl = "https://ollama.com/download"),
    Uuid.parse("104dcd8a-983c-5d56-9e14-3a238290c2c9") to ProviderMeta(ProviderTier.FREE_QUOTA, noKeyRequired = false, worksInIranWithoutVpn = true, signupUrl = "https://chat.avalai.ir/platform/api-keys", quotaNote = R.string.provider_quota_avalai),
    Uuid.parse("4473f8e9-12b2-5ebe-9c94-e4f46d039c3b") to ProviderMeta(ProviderTier.FREE_QUOTA, noKeyRequired = false, worksInIranWithoutVpn = true, signupUrl = "https://gapgpt.app/platform/tokens", quotaNote = R.string.provider_quota_gapgpt),
    Uuid.parse("f6c07569-4d0a-5e64-a8a5-dafc60771279") to ProviderMeta(ProviderTier.FREE_QUOTA, noKeyRequired = false, worksInIranWithoutVpn = true, signupUrl = "https://console.metisai.ir/api-keys", quotaNote = R.string.provider_quota_metis),
    Uuid.parse("6ab18148-c138-4394-a46f-1cd8c8ceaa6d") to ProviderMeta(ProviderTier.FREE, noKeyRequired = false, worksInIranWithoutVpn = false, signupUrl = "https://aistudio.google.com/apikey"),
    Uuid.parse("d5734028-d39b-4d41-9841-fd648d65440e") to ProviderMeta(ProviderTier.FREE, noKeyRequired = false, worksInIranWithoutVpn = false, signupUrl = "https://openrouter.ai/settings/keys"),
    Uuid.parse("56a94d29-c88b-41c5-8e09-38a7612d6cf8") to ProviderMeta(ProviderTier.FREE, noKeyRequired = false, worksInIranWithoutVpn = false, signupUrl = "https://cloud.siliconflow.cn/account/ak"),
    Uuid.parse("3bc40dc1-b11a-46fa-863b-6306971223be") to ProviderMeta(ProviderTier.FREE, noKeyRequired = false, worksInIranWithoutVpn = false, signupUrl = "https://open.bigmodel.cn/usercenter/apikeys"),
    Uuid.parse("1eeea727-9ee5-4cae-93e6-6fb01a4d051e") to ProviderMeta(ProviderTier.PAID, noKeyRequired = false, worksInIranWithoutVpn = false, signupUrl = "https://platform.openai.com/api-keys", quotaNote = R.string.provider_quota_openai),
    Uuid.parse("f099ad5b-ef03-446d-8e78-7e36787f780b") to ProviderMeta(ProviderTier.PAID, noKeyRequired = false, worksInIranWithoutVpn = false, signupUrl = "https://platform.deepseek.com/api_keys"),
    Uuid.parse("ff3cde7e-0f65-43d7-8fb2-6475c99f5990") to ProviderMeta(ProviderTier.PAID, noKeyRequired = false, worksInIranWithoutVpn = false, signupUrl = "https://console.x.ai"),
)

val ProviderSetting.meta: ProviderMeta? get() = PROVIDER_META[id]

enum class ProviderFilter { ALL, FREE, NO_VPN }

fun ProviderSetting.matches(filter: ProviderFilter): Boolean = when (filter) {
    ProviderFilter.ALL -> true
    ProviderFilter.FREE -> meta?.tier.isFreeUsable
    ProviderFilter.NO_VPN -> meta?.worksInIranWithoutVpn == true
}

@Composable
fun ProviderSignupDescription(id: String) {
    val meta = PROVIDER_META[Uuid.parse(id)] ?: return
    meta.quotaNote?.let { note ->
        Text(
            text = "\uD83C\uDF81 " + stringResource(note),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodySmall,
        )
    }
    val url = meta.signupUrl ?: return
    Text(
        text = buildAnnotatedString {
            append(stringResource(R.string.provider_get_api_key))
            append(" ")
            withLink(LinkAnnotation.Url(url)) {
                withStyle(SpanStyle(MaterialTheme.colorScheme.primary)) { append(url) }
            }
        }
    )
}
