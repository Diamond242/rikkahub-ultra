package me.rerere.rikkahub.ui.pages.setting

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.heightIn
import me.rerere.rikkahub.ui.components.richtext.MarkdownBlock
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toOffset
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.Code
import me.rerere.hugeicons.stroke.Earth
import me.rerere.hugeicons.stroke.File02
import me.rerere.hugeicons.stroke.GitFork
import me.rerere.hugeicons.stroke.Github
import me.rerere.hugeicons.stroke.Book01
import me.rerere.hugeicons.stroke.InLove
import me.rerere.hugeicons.stroke.TransactionHistory
import me.rerere.hugeicons.stroke.UserCircle
import me.rerere.hugeicons.stroke.SmartPhone01
import me.rerere.rikkahub.BuildConfig
import me.rerere.rikkahub.R
import me.rerere.rikkahub.Screen
import me.rerere.rikkahub.ui.components.nav.BackButton
import me.rerere.rikkahub.ui.components.ui.BrandTitle
import me.rerere.rikkahub.ui.components.ui.CardGroup
import me.rerere.rikkahub.ui.components.ui.CardGroupIcon
import me.rerere.rikkahub.ui.components.ui.drawAppLogo
import me.rerere.rikkahub.ui.components.ui.logoMorphShape
import me.rerere.rikkahub.ui.components.ui.rememberAppLogoLayers
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.theme.CustomColors
import me.rerere.rikkahub.utils.AppLinks
import me.rerere.rikkahub.utils.Changelog
import me.rerere.rikkahub.utils.SoundEffectPlayer
import me.rerere.rikkahub.utils.openUrl
import me.rerere.rikkahub.utils.plus
import me.rerere.ui.easteregg.EmojiBurstHost

@Composable
fun SettingAboutPage() {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val context = LocalContext.current
    val navController = LocalNavController.current
    val soundOptions = remember { listOf(R.raw.bingbingbing, R.raw.gangguan) }
    val soundEffectPlayer = remember(context) { SoundEffectPlayer(context) }
    DisposableEffect(soundEffectPlayer) {
        soundEffectPlayer.preload(*soundOptions.toIntArray())
        onDispose {
            soundEffectPlayer.release()
        }
    }
    val emojiOptions = remember {
        listOf(
            "🎉", "✨", "🌟", "💫", "🎊", "🥳", "🎈", "🎆", "🎇", "🧨",
            "🌈", "🧧", "🎁", "🍬", "🍭", "🍉", "🍓", "🍒", "🍍", "🥭",
            "🐱", "🐶", "🦊", "🐼", "🦁", "🐯", "🐵", "🦄",
            "❤️", "🧡", "💛", "💚", "💙", "💜",
            "🇨🇳", "🌏", "🌍", "🌎",
            "🤗", "🤩", "😆", "😺", "😸", "🤡",
            "💡", "🔥", "💥", "🚀", "⭐", "🌙"
        )
    }
    val logoLayers = rememberAppLogoLayers()
    // 表情从 logo 中心喷出，坐标要换算到 EmojiBurstHost 里
    var hostCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var logoCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var showChangelog by remember { mutableStateOf(false) }
    if (showChangelog) {
        ChangelogSheet(onDismiss = { showChangelog = false })
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.about_page_title))
                },
                navigationIcon = {
                    BackButton()
                },
                scrollBehavior = scrollBehavior,
                colors = CustomColors.topBarColors,
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = CustomColors.topBarColors.containerColor,
    ) { innerPadding ->
        EmojiBurstHost(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { hostCoordinates = it },
            emojiOptions = emojiOptions,
            burstCount = 12
        ) { onBurst ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = innerPadding + PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    val interactionSource = remember { MutableInteractionSource() }
                    val pressed by interactionSource.collectIsPressedAsState()
                    val logoScale by animateFloatAsState(
                        targetValue = if (pressed) 0.88f else 1f,
                        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
                    )
                    // 每点一次，轮廓变形到下一个形状
                    var shapeIndex by remember { mutableIntStateOf(0) }
                    val shapePosition by animateFloatAsState(
                        targetValue = shapeIndex.toFloat(),
                        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 8.dp, bottom = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(136.dp)
                                .semantics { contentDescription = "Logo" }
                                .onGloballyPositioned { logoCoordinates = it }
                                .graphicsLayer {
                                    scaleX = logoScale
                                    scaleY = logoScale
                                    shape = logoMorphShape(shapePosition)
                                    clip = true
                                }
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = LocalIndication.current,
                                ) {
                                    val host = hostCoordinates
                                    val logo = logoCoordinates
                                    if (host != null && logo != null && host.isAttached && logo.isAttached) {
                                        onBurst(host.localPositionOf(logo, logo.size.center.toOffset()))
                                    }
                                    soundEffectPlayer.play(soundOptions.random())
                                    shapeIndex++
                                }
                                .drawAppLogo(logoLayers)
                        )

                        BrandTitle()
                        Text(
                            text = stringResource(R.string.about_page_tagline),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                item {
                    val containerColor = MaterialTheme.colorScheme.primaryContainer
                    val contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    CardGroup {
                        item(
                            modifier = Modifier.combinedClickable(
                                onClick = { showChangelog = true },
                                onLongClick = { navController.navigate(Screen.Debug) },
                            ),
                            leadingContent = { CardGroupIcon(HugeIcons.Code, containerColor = containerColor, contentColor = contentColor) },
                            supportingContent = {
                                Text("${BuildConfig.VERSION_NAME} / ${BuildConfig.VERSION_CODE}")
                            },
                            headlineContent = { Text(stringResource(R.string.about_page_version)) },
                        )
                        item(
                            onClick = { showChangelog = true },
                            leadingContent = { CardGroupIcon(HugeIcons.TransactionHistory, containerColor = containerColor, contentColor = contentColor) },
                            supportingContent = { Text(stringResource(R.string.about_page_changelog_desc)) },
                            headlineContent = { Text(stringResource(R.string.about_page_changelog)) },
                        )
                        item(
                            leadingContent = { CardGroupIcon(HugeIcons.SmartPhone01, containerColor = containerColor, contentColor = contentColor) },
                            supportingContent = {
                                Text("${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL} / Android ${android.os.Build.VERSION.RELEASE} / SDK ${android.os.Build.VERSION.SDK_INT}")
                            },
                            headlineContent = { Text(stringResource(R.string.about_page_system)) },
                        )
                    }
                }

                item {
                    val containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    val contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    CardGroup(title = { Text(stringResource(R.string.about_page_project)) }) {
                        item(
                            onClick = { context.openUrl(AppLinks.DEVELOPER_WEBSITE) },
                            leadingContent = { CardGroupIcon(HugeIcons.UserCircle, containerColor = containerColor, contentColor = contentColor) },
                            supportingContent = { Text("${stringResource(R.string.about_page_developer_name)} · vahid.top") },
                            headlineContent = { Text(stringResource(R.string.about_page_developer)) },
                        )
                        item(
                            onClick = { context.openUrl(AppLinks.GITHUB) },
                            leadingContent = { CardGroupIcon(HugeIcons.Github, containerColor = containerColor, contentColor = contentColor) },
                            supportingContent = { Text(AppLinks.GITHUB) },
                            headlineContent = { Text(stringResource(R.string.about_page_github)) },
                        )
                        item(
                            onClick = { context.openUrl(AppLinks.DOCS) },
                            leadingContent = { CardGroupIcon(HugeIcons.Book01, containerColor = containerColor, contentColor = contentColor) },
                            supportingContent = { Text(AppLinks.DOCS) },
                            headlineContent = { Text(stringResource(R.string.setting_page_documentation)) },
                        )
                        item(
                            onClick = { context.openUrl(AppLinks.LICENSE) },
                            leadingContent = { CardGroupIcon(HugeIcons.File02, containerColor = containerColor, contentColor = contentColor) },
                            supportingContent = { Text(stringResource(R.string.about_page_license_desc)) },
                            headlineContent = { Text(stringResource(R.string.about_page_license)) },
                        )
                    }
                }

                item {
                    val containerColor = MaterialTheme.colorScheme.secondaryContainer
                    val contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    CardGroup {
                        item(
                            onClick = { context.openUrl(AppLinks.UPSTREAM_GITHUB) },
                            leadingContent = { CardGroupIcon(HugeIcons.GitFork, containerColor = containerColor, contentColor = contentColor) },
                            supportingContent = { Text(stringResource(R.string.about_page_based_on_desc)) },
                            headlineContent = { Text(stringResource(R.string.about_page_based_on)) },
                        )
                        item(
                            onClick = { navController.navigate(Screen.SettingDonate) },
                            leadingContent = { CardGroupIcon(HugeIcons.InLove, containerColor = containerColor, contentColor = contentColor) },
                            supportingContent = { Text(stringResource(R.string.donate_page_hero_short)) },
                            headlineContent = { Text(stringResource(R.string.setting_page_donate)) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChangelogSheet(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val language = context.resources.configuration.locales[0].language
    val text = remember(language) {
        Changelog.loadBundled(context)?.let { Changelog.localize(it, language) }
    }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberBottomSheetState(initialValue = SheetValue.Hidden, enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.about_page_changelog),
                style = MaterialTheme.typography.titleLarge,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                MarkdownBlock(
                    content = text ?: stringResource(R.string.about_page_changelog_unavailable),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            OutlinedButton(
                onClick = { context.openUrl(AppLinks.CHANGELOG) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.update_card_view_release))
            }
        }
    }
}