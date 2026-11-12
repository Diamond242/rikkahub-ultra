package me.rerere.rikkahub.ui.pages.chat

import me.rerere.rikkahub.data.datastore.labelRes
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.rerere.ai.provider.Model
import me.rerere.ai.provider.ModelType
import me.rerere.ai.provider.ProviderManager
import me.rerere.ai.provider.ProviderSetting
import me.rerere.rikkahub.R
import me.rerere.rikkahub.data.datastore.ProviderTier
import me.rerere.rikkahub.data.datastore.meta
import me.rerere.rikkahub.utils.friendlyErrorKind
import me.rerere.rikkahub.utils.messageRes
import me.rerere.rikkahub.utils.withApiKey
import org.koin.compose.koinInject

private const val PREFS = "onboarding"
private const val KEY_DONE = "done_v1"

fun isOnboardingDone(context: Context): Boolean =
    context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_DONE, false)

private fun markOnboardingDone(context: Context) =
    context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY_DONE, true).apply()

/** Providers suggested on first run: Iran-reachable first, then free tier, then trial. */
fun onboardingCandidates(providers: List<ProviderSetting>): List<ProviderSetting> =
    providers.filter { it is ProviderSetting.ApiKey && it.meta != null && it.meta?.noKeyRequired != true }
        .sortedWith(compareBy({ if (it.meta!!.worksInIranWithoutVpn) 0 else 1 }, { it.meta!!.tier.ordinal }))
        .filter { it.meta!!.worksInIranWithoutVpn || it.meta!!.tier != ProviderTier.PAID }

/**
 * One-time first-run wizard: pick a suggested provider, open its key page, paste key, test it, finish.
 * [onFinish] receives the updated provider and the model to start chatting with.
 */
@Composable
fun OnboardingDialog(
    providers: List<ProviderSetting>,
    onFinish: (ProviderSetting, Model?) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val providerManager = koinInject<ProviderManager>()
    val scope = rememberCoroutineScope()
    val candidates = remember(providers) { onboardingCandidates(providers) }
    var selected by remember { mutableStateOf(candidates.firstOrNull()) }
    var key by remember { mutableStateOf("") }
    var testing by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<String?>(null) }
    var fetched by remember { mutableStateOf<List<Model>>(emptyList()) }
    val okText = stringResource(R.string.key_test_success)
    val failPrefix = stringResource(R.string.key_test_failed)

    fun finish(save: Boolean) {
        markOnboardingDone(context)
        val p = selected
        if (save && p != null && key.isNotBlank()) {
            val existingIds = p.models.map { it.modelId }.toSet()
            val newModels = fetched.filter { it.type == ModelType.CHAT && it.modelId !in existingIds }.take(30)
            val updated = p.withApiKey(key.trim()).copyProvider(models = p.models + newModels, enabled = true)
            onFinish(updated, updated.models.firstOrNull { it.type == ModelType.CHAT })
        } else onDismiss()
    }

    AlertDialog(
        onDismissRequest = {},
        title = { Text(stringResource(R.string.onboarding_title)) },
        text = {
            Column(
                modifier = Modifier.heightIn(max = 480.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(stringResource(R.string.onboarding_intro), style = MaterialTheme.typography.bodyMedium)
                candidates.forEach { p ->
                    Row(
                        Modifier.fillMaxWidth().clickable { selected = p; result = null; fetched = emptyList() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = selected?.id == p.id, onClick = { selected = p; result = null; fetched = emptyList() })
                        Column {
                            Text(p.name, style = MaterialTheme.typography.bodyLarge)
                            val m = p.meta!!
                            Text(
                                buildList {
                                    add(context.getString(m.tier.labelRes))
                                    m.quotaNote?.let { add(context.getString(it)) }
                                    if (m.worksInIranWithoutVpn) add(context.getString(R.string.provider_filter_no_vpn))
                                }.joinToString(" · "),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                selected?.meta?.signupUrl?.let { url ->
                    OutlinedButton(onClick = { runCatching { uriHandler.openUri(url) } }, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.onboarding_get_key))
                    }
                }
                OutlinedTextField(
                    value = key, onValueChange = { key = it; result = null },
                    label = { Text(stringResource(R.string.onboarding_paste_key)) },
                    singleLine = true, modifier = Modifier.fillMaxWidth()
                )
                Button(
                    enabled = !testing && key.isNotBlank() && selected != null,
                    onClick = {
                        val p = selected!!.withApiKey(key.trim())
                        testing = true; result = null
                        scope.launch {
                            runCatching { providerManager.getProviderByType(p).listModels(p) }
                                .onSuccess { fetched = it; result = okText.format(it.size) }
                                .onFailure { e ->
                                    if (e is kotlinx.coroutines.CancellationException) throw e
                                    val k = e.friendlyErrorKind()
                                    result = failPrefix + " " + (k?.let { context.getString(it.messageRes()) } ?: (e.message ?: e.javaClass.simpleName))
                                }
                            testing = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text(stringResource(if (testing) R.string.key_test_running else R.string.key_test_button)) }
                result?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
            }
        },
        confirmButton = {
            TextButton(enabled = key.isNotBlank() && selected != null, onClick = { finish(true) }) {
                Text(stringResource(R.string.onboarding_finish))
            }
        },
        dismissButton = { TextButton(onClick = { finish(false) }) { Text(stringResource(R.string.onboarding_skip)) } }
    )
}