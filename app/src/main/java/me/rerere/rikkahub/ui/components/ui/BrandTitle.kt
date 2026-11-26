package me.rerere.rikkahub.ui.components.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified

/** Brand name shown wherever the app presents itself as "RikkaHub". */
const val BRAND_NAME = "RikkaHub"

/** Suffix that marks this fork. */
const val BRAND_SUFFIX = "Ultra"

/** Green used for the "Ultra" mark; slightly lighter on dark surfaces for contrast. */
@Composable
fun ultraGreen(): Color =
    if (MaterialTheme.colorScheme.surface.luminance() < 0.5f) Color(0xFF4ADE80) else Color(0xFF16A34A)

/**
 * "RikkaHub" followed by a smaller green "Ultra" that sits on the baseline (bottom-right of the name).
 * Always laid out left-to-right so the mark stays after the name in RTL languages too.
 */
@Composable
fun BrandTitle(
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.headlineLargeEmphasized.copy(fontWeight = FontWeight.Black),
    color: Color = Color.Unspecified,
    suffixScale: Float = 0.45f,
) {
    val suffixSize = if (style.fontSize.isSpecified) style.fontSize * suffixScale else MaterialTheme.typography.labelMedium.fontSize
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(modifier = modifier, verticalAlignment = Alignment.Bottom) {
            Text(
                text = BRAND_NAME,
                style = style,
                color = color,
                maxLines = 1,
                modifier = Modifier.alignByBaseline(),
            )
            Text(
                text = BRAND_SUFFIX,
                style = style.copy(fontSize = suffixSize, fontWeight = FontWeight.Bold, letterSpacing = style.letterSpacing),
                color = ultraGreen(),
                maxLines = 1,
                modifier = Modifier
                    .alignByBaseline()
                    .padding(start = 3.dp),
            )
        }
    }
}