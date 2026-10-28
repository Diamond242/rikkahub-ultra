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
import me.rerere.rikkahub.R
import me.rerere.rikkahub.ui.components.richtext.MarkdownBlock
import kotlin.uuid.Uuid

/**
 * 推荐的提供商列表，在提供商设置页右上角的推荐 Sheet 中展示。
 */
val RECOMMENDED_PROVIDERS: List<ProviderSetting> = listOf(
    ProviderSetting.OpenAI(
        id = Uuid.parse("2a05506f-3a59-450a-a493-33a82bc85a81"),
        name = "APIMart",
        baseUrl = "https://api.apimart.ai/v1",
        apiKey = "",
        enabled = true,
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
    ),
    ProviderSetting.OpenAI(
        id = Uuid.parse("aecf04fd-cb5c-4582-aed2-e8bf393923fd"),
        name = "随想AI网关",
        baseUrl = "https://sui-xiang.com/v1",
        apiKey = "",
        enabled = true,
        description = {
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.provider_suixiang_desc))
                    appendLine()
                    append(stringResource(R.string.provider_label_website))
                    withLink(LinkAnnotation.Url("https://sui-xiang.com")) {
                        withStyle(SpanStyle(MaterialTheme.colorScheme.primary)) {
                            append("https://sui-xiang.com")
                        }
                    }
                }
            )
        },
    ),
    ProviderSetting.OpenAI(
        id = Uuid.parse("afbc54ad-807e-4455-9594-7d7a546356ad"),
        name = "MaruCode",
        baseUrl = "https://api.muteki.site/v1",
        apiKey = "",
        enabled = true,
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
    ProviderSetting.OpenAI(
        id = Uuid.parse("228e2443-f3bb-4222-970c-2c800aff514d"),
        name = "钠API",
        baseUrl = "https://naapi.cc/v1",
        apiKey = "",
        enabled = true,
        description = {
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.provider_naapi_desc))
                    appendLine()
                    append(stringResource(R.string.provider_label_website))
                    withLink(LinkAnnotation.Url("https://naapi.cc/register?aff=rikkahub")) {
                        withStyle(SpanStyle(MaterialTheme.colorScheme.primary)) {
                            append("https://naapi.cc")
                        }
                    }
                    appendLine()
                    append(stringResource(R.string.provider_label_top_up))
                    withLink(LinkAnnotation.Url("https://naapi.cc/console/topup")) {
                        withStyle(SpanStyle(MaterialTheme.colorScheme.primary)) {
                            append("https://naapi.cc/console/topup")
                        }
                    }
                }
            )
        },
    ),
)
