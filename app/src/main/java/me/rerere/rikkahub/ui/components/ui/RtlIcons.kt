package me.rerere.rikkahub.ui.components.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.ArrowLeft01
import me.rerere.hugeicons.stroke.ArrowRight01

/** Arrow pointing toward the start edge (back/previous); mirrored automatically in RTL layouts. */
@Composable
fun startArrowIcon(): ImageVector =
    if (LocalLayoutDirection.current == LayoutDirection.Rtl) HugeIcons.ArrowRight01 else HugeIcons.ArrowLeft01

/** Arrow pointing toward the end edge (forward/next/open); mirrored automatically in RTL layouts. */
@Composable
fun endArrowIcon(): ImageVector =
    if (LocalLayoutDirection.current == LayoutDirection.Rtl) HugeIcons.ArrowLeft01 else HugeIcons.ArrowRight01