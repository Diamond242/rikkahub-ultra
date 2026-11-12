package me.rerere.rikkahub.ui.components.ai

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.InformationCircle
import me.rerere.rikkahub.R
import me.rerere.rikkahub.data.datastore.builtInAssistantInfoRes
import me.rerere.rikkahub.data.datastore.displayName
import me.rerere.rikkahub.data.model.Assistant

/** "i" button that explains a built-in assistant. Renders nothing for user-created assistants. */
@Composable
fun AssistantInfoButton(assistant: Assistant, modifier: Modifier = Modifier) {
    val infoRes = builtInAssistantInfoRes(assistant.id) ?: return
    var show by remember { mutableStateOf(false) }
    IconButton(onClick = { show = true }, modifier = modifier) {
        Icon(HugeIcons.InformationCircle, contentDescription = stringResource(R.string.assistant_builtin_info_title))
    }
    if (show) {
        AlertDialog(
            onDismissRequest = { show = false },
            icon = { Icon(HugeIcons.InformationCircle, contentDescription = null) },
            title = { Text(assistant.displayName()) },
            text = { Text(stringResource(infoRes)) },
            confirmButton = {
                TextButton(onClick = { show = false }) { Text(stringResource(R.string.assistant_builtin_info_ok)) }
            },
        )
    }
}