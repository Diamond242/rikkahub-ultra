package me.rerere.rikkahub.ui.pages.setting.components

import android.graphics.BitmapFactory
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.rerere.ai.provider.ProviderSetting
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.Key01
import me.rerere.rikkahub.R
import me.rerere.rikkahub.data.datastore.meta
import kotlin.uuid.Uuid

/** Step-by-step "where do I get the key" guide; optional screenshot in assets/key_guides/<slug>.png. */
data class KeyGuide(val slug: String, val name: String, @StringRes val steps: Int)

val KEY_GUIDES: Map<Uuid, KeyGuide> = mapOf(
    Uuid.parse("4473f8e9-12b2-5ebe-9c94-e4f46d039c3b") to KeyGuide("gapgpt", "GapGPT", R.string.key_guide_gapgpt),
    Uuid.parse("7803fc83-af3d-5e09-8821-33cdcf1682f6") to KeyGuide("mistral", "Mistral", R.string.key_guide_mistral),
    Uuid.parse("f134f417-7c91-5091-8879-4131dfa50e2d") to KeyGuide("sambanova", "SambaNova", R.string.key_guide_sambanova),
    Uuid.parse("76c5f600-a6b7-5cb8-b6d9-0d3ef796db7a") to KeyGuide("nvidia", "NVIDIA NIM", R.string.key_guide_nvidia),
    Uuid.parse("97fb1325-5f4f-5981-8c0b-f57346d29665") to KeyGuide("groq", "Groq", R.string.key_guide_groq),
    Uuid.parse("d5734028-d39b-4d41-9841-fd648d65440e") to KeyGuide("openrouter", "OpenRouter", R.string.key_guide_openrouter),
    Uuid.parse("ef29c2ad-ea6c-5bcb-9f22-0c66c9c7bd7e") to KeyGuide("cohere", "Cohere", R.string.key_guide_cohere),
    Uuid.parse("531dac70-e404-5164-be97-8e44c19c92ae") to KeyGuide("cerebras", "Cerebras", R.string.key_guide_cerebras),
    Uuid.parse("47814e33-8bb6-543c-99a7-785ed6f1b29a") to KeyGuide("siliconflow", "SiliconFlow", R.string.key_guide_siliconflow),
    Uuid.parse("6ab18148-c138-4394-a46f-1cd8c8ceaa6d") to KeyGuide("gemini", "Google Gemini", R.string.key_guide_gemini),
    Uuid.parse("104dcd8a-983c-5d56-9e14-3a238290c2c9") to KeyGuide("avalai", "AvalAI", R.string.key_guide_avalai),
    Uuid.parse("f6c07569-4d0a-5e64-a8a5-dafc60771279") to KeyGuide("metisai", "MetisAI", R.string.key_guide_metisai),
    Uuid.parse("6b381ff2-51c6-5339-bde5-40f564be2d28") to KeyGuide("ollama", "Ollama", R.string.key_guide_ollama),
    Uuid.parse("65e59018-c4ab-5087-ad1e-6dc2a665c331") to KeyGuide("llm7", "LLM7", R.string.key_guide_llm7),
    Uuid.parse("4efb9dd2-f0f0-5b71-bc12-ef3273de4e30") to KeyGuide("pollinations", "Pollinations", R.string.key_guide_pollinations),
    Uuid.parse("33fb36c5-d946-5a3c-ba42-103e96966f7e") to KeyGuide("zai", "Z.ai", R.string.key_guide_zai),
)

@Composable
fun KeyGuideButton(provider: ProviderSetting) {
    var show by rememberSaveable { mutableStateOf(false) }
    IconButton(onClick = { show = true }) {
        Icon(HugeIcons.Key01, stringResource(R.string.key_guide_button))
    }
    if (show) KeyGuideDialog(provider) { show = false }
}

@Composable
fun KeyGuideDialog(provider: ProviderSetting, onDismiss: () -> Unit) {
    val guide = KEY_GUIDES[provider.id]
    val url = provider.meta?.signupUrl
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val image = remember(guide) {
        guide?.let {
            runCatching {
                context.assets.open("key_guides/${it.slug}.png").use { s -> BitmapFactory.decodeStream(s) }
            }.getOrNull()?.asImageBitmap()
        }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.key_guide_title, guide?.name ?: provider.name)) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Text(stringResource(guide?.steps ?: R.string.key_guide_none))
                if (image != null) {
                    Image(
                        bitmap = image,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxWidth().heightIn(max = 240.dp).padding(top = 12.dp),
                    )
                }
            }
        },
        confirmButton = {
            if (url != null) TextButton(onClick = { runCatching { uriHandler.openUri(url) } }) {
                Text(stringResource(R.string.key_guide_open_site))
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } },
    )
}