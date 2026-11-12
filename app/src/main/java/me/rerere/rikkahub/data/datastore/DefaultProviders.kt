package me.rerere.rikkahub.data.datastore

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import me.rerere.ai.provider.ProviderSetting
import me.rerere.ai.provider.QuotaQuery
import me.rerere.rikkahub.R
import me.rerere.rikkahub.ui.components.richtext.MarkdownBlock
import kotlin.uuid.Uuid

val DEFAULT_AUTO_MODEL_ID = Uuid.parse("b7055fb4-39f9-4042-a88a-0d80ed76cf08")

val DEFAULT_PROVIDERS = listOf(
    ProviderSetting.OpenAI(
        id = Uuid.parse("1eeea727-9ee5-4cae-93e6-6fb01a4d051e"),
        name = "OpenAI",
        baseUrl = "https://api.openai.com/v1",
        apiKey = "",
        builtIn = true
    ),
    ProviderSetting.Google(
        id = Uuid.parse("6ab18148-c138-4394-a46f-1cd8c8ceaa6d"),
        name = "Gemini",
        apiKey = "",
        enabled = true,
        builtIn = true
    ),
    ProviderSetting.OpenAI(
        id = Uuid.parse("1b1395ed-b702-4aeb-8bc1-b681c4456953"),
        name = "AiHubMix",
        baseUrl = "https://aihubmix.com/v1",
        apiKey = "",
        enabled = true,
        builtIn = true,
        description = {
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.provider_aihubmix_desc))
                    appendLine()
                    append(stringResource(R.string.provider_label_website))
                    withLink(LinkAnnotation.Url("https://aihubmix.com?aff=pG7r")) {
                        withStyle(SpanStyle(MaterialTheme.colorScheme.primary)) {
                            append("https://aihubmix.com")
                        }
                    }
                    appendLine()
                    append(stringResource(R.string.provider_label_top_up))
                    withLink(LinkAnnotation.Url("https://console.aihubmix.com/topup")) {
                        withStyle(SpanStyle(MaterialTheme.colorScheme.primary)) {
                            append("https://console.aihubmix.com/topup")
                        }
                    }
                }
            )
        },
        shortDescription = {
            Text(
                text = stringResource(R.string.provider_aihubmix_short)
            )
        },
    ),
    ProviderSetting.OpenAI(
        id = Uuid.parse("2a05506f-3a59-450a-a493-33a82bc85a81"),
        name = "APIMart",
        baseUrl = "https://api.apimart.ai/v1",
        apiKey = "",
        enabled = false,
        builtIn = true,
        description = {
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.provider_apimart_desc))
                    appendLine()
                    withLink(LinkAnnotation.Url("https://go.apimart.ai/gh-rikkahub")) {
                        withStyle(SpanStyle(MaterialTheme.colorScheme.primary)) {
                            append(stringResource(R.string.provider_apimart_signup))
                        }
                    }
                }
            )
        },
        shortDescription = {
            Text(stringResource(R.string.provider_apimart_short))
        },
    ),
    ProviderSetting.OpenAI(
        id = Uuid.parse("56a94d29-c88b-41c5-8e09-38a7612d6cf8"),
        name = "硅基流动",
        baseUrl = "https://api.siliconflow.cn/v1",
        apiKey = "",
        builtIn = true,
        description = {
            MarkdownBlock(
                content = """
                    ${stringResource(R.string.silicon_flow_description)}
                    ${stringResource(R.string.silicon_flow_website)}
                """.trimIndent()
            )
        },
    ),
    ProviderSetting.OpenAI(
        id = Uuid.parse("f099ad5b-ef03-446d-8e78-7e36787f780b"),
        name = "DeepSeek",
        baseUrl = "https://api.deepseek.com/v1",
        apiKey = "",
        builtIn = true,
        quotaQuery = QuotaQuery(
            enabled = true,
            url = "{{baseUrl}}/user/balance",
            resultPath = "balance_infos[0].total_balance"
        )
    ),
    ProviderSetting.OpenAI(
        id = Uuid.parse("d6c4d8c6-3f62-4ca9-a6f3-7ade6b15ecc3"),
        name = "月之暗面",
        baseUrl = "https://api.moonshot.cn/v1",
        apiKey = "",
        enabled = true,
        builtIn = true,
        quotaQuery = QuotaQuery(
            enabled = true,
            url = "{{baseUrl}}/users/me/balance",
            resultPath = "data.available_balance"
        )
    ),
    ProviderSetting.OpenAI(
        id = Uuid.parse("d5734028-d39b-4d41-9841-fd648d65440e"),
        name = "OpenRouter",
        baseUrl = "https://openrouter.ai/api/v1",
        apiKey = "",
        builtIn = true,
        quotaQuery = QuotaQuery(
            enabled = true,
            url = "{{baseUrl}}/credits",
            resultPath = "data.total_credits - data.total_usage",
        )
    ),
    ProviderSetting.OpenAI(
        id = Uuid.parse("386e0f29-8228-4512-affe-8fd8add82d88"),
        name = "Vercel AI Gateway",
        baseUrl = "https://ai-gateway.vercel.sh/v1",
        apiKey = "",
        enabled = false,
        builtIn = true,
        quotaQuery = QuotaQuery(
            enabled = true,
            url = "{{baseUrl}}/credits",
            resultPath = "balance",
        )
    ),
    ProviderSetting.OpenAI(
        id = Uuid.parse("f76cae46-069a-4334-ab8e-224e4979e58c"),
        name = "阿里云百炼",
        baseUrl = "https://dashscope.aliyuncs.com/compatible-mode/v1",
        apiKey = "",
        enabled = false,
        builtIn = true
    ),
    ProviderSetting.OpenAI(
        id = Uuid.parse("3dfd6f9b-f9d9-417f-80c1-ff8d77184191"),
        name = "火山引擎",
        baseUrl = "https://ark.cn-beijing.volces.com/api/v3",
        apiKey = "",
        enabled = false,
        builtIn = true
    ),
    ProviderSetting.OpenAI(
        id = Uuid.parse("3bc40dc1-b11a-46fa-863b-6306971223be"),
        name = "智谱AI开放平台",
        baseUrl = "https://open.bigmodel.cn/api/paas/v4",
        apiKey = "",
        enabled = false,
        builtIn = true
    ),
    ProviderSetting.OpenAI(
        id = Uuid.parse("f4f8870e-82d3-495b-9b64-d58e508b3b2c"),
        name = "阶跃星辰",
        baseUrl = "https://api.stepfun.com/v1",
        apiKey = "",
        enabled = false,
        builtIn = true
    ),
    ProviderSetting.OpenAI(
        id = Uuid.parse("da93779f-3956-48cc-82ef-67bb482eaaf7"),
        name = "302.AI",
        baseUrl = "https://api.302.ai/v1",
        apiKey = "",
        enabled = false,
        builtIn = true,
        description = {
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.provider_302ai_desc))
                    withLink(LinkAnnotation.Url("https://302.ai/")) {
                        withStyle(SpanStyle(MaterialTheme.colorScheme.primary)) {
                            append("https://302.ai/")
                        }
                    }
                }
            )
        }
    ),
    ProviderSetting.OpenAI(
        id = Uuid.parse("ef5d149b-8e34-404b-818c-6ec242e5c3c5"),
        name = "腾讯Hunyuan",
        baseUrl = "https://tokenhub.tencentmaas.com/v1",
        apiKey = "",
        enabled = false,
        builtIn = true
    ),
    ProviderSetting.OpenAI(
        id = Uuid.parse("ff3cde7e-0f65-43d7-8fb2-6475c99f5990"),
        name = "xAI",
        baseUrl = "https://api.x.ai/v1",
        apiKey = "",
        enabled = false,
        builtIn = true,
        useResponseApi = true,
    ),
    ProviderSetting.OpenAI(
        id = Uuid.parse("aecf04fd-cb5c-4582-aed2-e8bf393923fd"),
        name = "随想AI网关",
        baseUrl = "https://sui-xiang.com/v1",
        apiKey = "",
        enabled = false,
        builtIn = true,
        description = {
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.provider_suixiang_desc) + "\n")
                    append(stringResource(R.string.provider_label_website))
                    withLink(LinkAnnotation.Url("https://sui-xiang.com")) {
                        withStyle(SpanStyle(MaterialTheme.colorScheme.primary)) {
                            append("https://sui-xiang.com")
                        }
                    }
                }
            )
        },
        shortDescription = {
            Text(
                text = stringResource(R.string.provider_suixiang_short)
            )
        },
    ),
    ProviderSetting.OpenAI(
        id = Uuid.parse("afbc54ad-807e-4455-9594-7d7a546356ad"),
        name = "MaruCode",
        baseUrl = "https://api.muteki.site/v1",
        apiKey = "",
        enabled = false,
        builtIn = true,
        description = {
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.provider_marucode_desc))
                    appendLine()
                    withLink(LinkAnnotation.Url("https://api.muteki.site/register?aff=Rikkahub&promo=Rikkahub")) {
                        withStyle(SpanStyle(MaterialTheme.colorScheme.primary)) {
                            append(stringResource(R.string.provider_marucode_signup_bonus))
                        }
                    }
                    appendLine()
                    withLink(LinkAnnotation.Url("https://images-2.muteki.site")) {
                        withStyle(SpanStyle(MaterialTheme.colorScheme.primary)) {
                            append(stringResource(R.string.provider_marucode_image_studio))
                        }
                    }
                }
            )
        },
        useResponseApi = true,
    ),
    ProviderSetting.Claude(
        id = Uuid.parse("b4deabea-20fb-4101-a74c-65679c7e4754"),
        name = "MiniMax",
        baseUrl = "https://api.minimaxi.com/anthropic/v1",
        apiKey = "",
        enabled = false,
        builtIn = true,
    ),
    ProviderSetting.OpenAI(
        id = Uuid.parse("a2bafe83-eaf8-47bf-a8c7-3dd82d89f637"),
        name = "MIMO",
        baseUrl = "https://api.xiaomimimo.com/v1",
        apiKey = "",
        enabled = false,
        builtIn = true,
    ),
    ProviderSetting.OpenAI(
        id = Uuid.parse("97fb1325-5f4f-5981-8c0b-f57346d29665"),
        name = "Groq",
        baseUrl = "https://api.groq.com/openai/v1",
        apiKey = "",
        enabled = false,
        builtIn = true,
        description = { ProviderSignupDescription("97fb1325-5f4f-5981-8c0b-f57346d29665") },
    ),
    ProviderSetting.OpenAI(
        id = Uuid.parse("76c5f600-a6b7-5cb8-b6d9-0d3ef796db7a"),
        name = "NVIDIA NIM",
        baseUrl = "https://integrate.api.nvidia.com/v1",
        apiKey = "",
        enabled = false,
        builtIn = true,
        description = { ProviderSignupDescription("76c5f600-a6b7-5cb8-b6d9-0d3ef796db7a") },
    ),
    ProviderSetting.OpenAI(
        id = Uuid.parse("7803fc83-af3d-5e09-8821-33cdcf1682f6"),
        name = "Mistral",
        baseUrl = "https://api.mistral.ai/v1",
        apiKey = "",
        enabled = false,
        builtIn = true,
        description = { ProviderSignupDescription("7803fc83-af3d-5e09-8821-33cdcf1682f6") },
    ),
    ProviderSetting.OpenAI(
        id = Uuid.parse("ef29c2ad-ea6c-5bcb-9f22-0c66c9c7bd7e"),
        name = "Cohere",
        baseUrl = "https://api.cohere.ai/compatibility/v1",
        apiKey = "",
        enabled = false,
        builtIn = true,
        description = { ProviderSignupDescription("ef29c2ad-ea6c-5bcb-9f22-0c66c9c7bd7e") },
    ),
    ProviderSetting.OpenAI(
        id = Uuid.parse("33fb36c5-d946-5a3c-ba42-103e96966f7e"),
        name = "Z.ai",
        baseUrl = "https://api.z.ai/api/paas/v4",
        apiKey = "",
        enabled = false,
        builtIn = true,
        description = { ProviderSignupDescription("33fb36c5-d946-5a3c-ba42-103e96966f7e") },
    ),
    ProviderSetting.OpenAI(
        id = Uuid.parse("47814e33-8bb6-543c-99a7-785ed6f1b29a"),
        name = "SiliconFlow (Global)",
        baseUrl = "https://api.siliconflow.com/v1",
        apiKey = "",
        enabled = false,
        builtIn = true,
        description = { ProviderSignupDescription("47814e33-8bb6-543c-99a7-785ed6f1b29a") },
    ),
    ProviderSetting.OpenAI(
        id = Uuid.parse("65e59018-c4ab-5087-ad1e-6dc2a665c331"),
        name = "LLM7",
        baseUrl = "https://api.llm7.io/v1",
        apiKey = "",
        enabled = false,
        builtIn = true,
        description = { ProviderSignupDescription("65e59018-c4ab-5087-ad1e-6dc2a665c331") },
    ),
    ProviderSetting.OpenAI(
        id = Uuid.parse("4efb9dd2-f0f0-5b71-bc12-ef3273de4e30"),
        name = "Pollinations",
        baseUrl = "https://gen.pollinations.ai/v1",
        apiKey = "",
        enabled = false,
        builtIn = true,
        description = { ProviderSignupDescription("4efb9dd2-f0f0-5b71-bc12-ef3273de4e30") },
    ),
    ProviderSetting.OpenAI(
        id = Uuid.parse("531dac70-e404-5164-be97-8e44c19c92ae"),
        name = "Cerebras",
        baseUrl = "https://api.cerebras.ai/v1",
        apiKey = "",
        enabled = false,
        builtIn = true,
        description = { ProviderSignupDescription("531dac70-e404-5164-be97-8e44c19c92ae") },
    ),
    ProviderSetting.OpenAI(
        id = Uuid.parse("f134f417-7c91-5091-8879-4131dfa50e2d"),
        name = "SambaNova",
        baseUrl = "https://api.sambanova.ai/v1",
        apiKey = "",
        enabled = false,
        builtIn = true,
        description = { ProviderSignupDescription("f134f417-7c91-5091-8879-4131dfa50e2d") },
    ),
    ProviderSetting.Claude(
        id = Uuid.parse("e7152bdc-7cca-5acf-ab4a-82ff25541f56"),
        name = "Anthropic",
        baseUrl = "https://api.anthropic.com/v1",
        apiKey = "",
        enabled = false,
        builtIn = true,
        description = { ProviderSignupDescription("e7152bdc-7cca-5acf-ab4a-82ff25541f56") },
    ),
    ProviderSetting.OpenAI(
        id = Uuid.parse("6b381ff2-51c6-5339-bde5-40f564be2d28"),
        name = "Ollama",
        baseUrl = "http://localhost:11434/v1",
        apiKey = "",
        enabled = false,
        builtIn = true,
        description = { ProviderSignupDescription("6b381ff2-51c6-5339-bde5-40f564be2d28") },
    ),
    ProviderSetting.OpenAI(
        id = Uuid.parse("104dcd8a-983c-5d56-9e14-3a238290c2c9"),
        name = "AvalAI",
        baseUrl = "https://api.avalai.ir/v1",
        apiKey = "",
        enabled = false,
        builtIn = true,
        description = { ProviderSignupDescription("104dcd8a-983c-5d56-9e14-3a238290c2c9") },
    ),
    ProviderSetting.OpenAI(
        id = Uuid.parse("4473f8e9-12b2-5ebe-9c94-e4f46d039c3b"),
        name = "GapGPT",
        baseUrl = "https://api.gapgpt.app/v1",
        apiKey = "",
        enabled = false,
        builtIn = true,
        description = { ProviderSignupDescription("4473f8e9-12b2-5ebe-9c94-e4f46d039c3b") },
    ),
    ProviderSetting.OpenAI(
        id = Uuid.parse("f6c07569-4d0a-5e64-a8a5-dafc60771279"),
        name = "Metis",
        baseUrl = "https://api.metisai.ir/openai/v1",
        apiKey = "",
        enabled = false,
        builtIn = true,
        description = { ProviderSignupDescription("f6c07569-4d0a-5e64-a8a5-dafc60771279") },
    ),
)
