package me.rerere.rikkahub.data.datastore

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import me.rerere.rikkahub.R
import me.rerere.rikkahub.data.model.Assistant
import kotlin.uuid.Uuid

/** Localized name of a built-in assistant, or null for user-created ones. */
@StringRes
fun builtInAssistantNameRes(id: Uuid): Int? = when (id) {
    PLAIN_ASSISTANT_ID -> R.string.assistant_builtin_simple_name
    SMART_ASSISTANT_ID -> R.string.assistant_builtin_smart_name
    else -> null
}

/** Plain-language explanation of a built-in assistant, or null for user-created ones. */
@StringRes
fun builtInAssistantInfoRes(id: Uuid): Int? = when (id) {
    PLAIN_ASSISTANT_ID -> R.string.assistant_builtin_simple_info
    SMART_ASSISTANT_ID -> R.string.assistant_builtin_smart_info
    else -> null
}

/**
 * Name shown in the UI. A blank stored name falls back to the built-in name for the id
 * (so it follows the app language), then to the generic "default assistant" label.
 */
fun Assistant.displayName(context: Context): String =
    name.ifBlank { context.getString(builtInAssistantNameRes(id) ?: R.string.assistant_page_default_assistant) }

@Composable
fun Assistant.displayName(): String =
    name.ifBlank { stringResource(builtInAssistantNameRes(id) ?: R.string.assistant_page_default_assistant) }