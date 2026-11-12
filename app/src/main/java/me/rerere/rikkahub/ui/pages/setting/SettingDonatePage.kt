package me.rerere.rikkahub.ui.pages.setting

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.Alert02
import me.rerere.hugeicons.stroke.Copy01
import me.rerere.hugeicons.stroke.Github
import me.rerere.hugeicons.stroke.InLove
import me.rerere.hugeicons.stroke.MoneyBag02
import me.rerere.rikkahub.R
import me.rerere.rikkahub.data.model.Sponsor
import me.rerere.rikkahub.data.remote.SponsorAPI
import me.rerere.rikkahub.ui.components.nav.BackButton
import me.rerere.rikkahub.ui.components.ui.QRCode
import me.rerere.rikkahub.ui.components.ui.ultraGreen
import me.rerere.rikkahub.ui.theme.CustomColors
import me.rerere.rikkahub.utils.AppLinks
import me.rerere.rikkahub.utils.UiState
import me.rerere.rikkahub.utils.onError
import me.rerere.rikkahub.utils.onLoading
import me.rerere.rikkahub.utils.onSuccess
import me.rerere.rikkahub.utils.openUrl
import me.rerere.rikkahub.utils.writeClipboardText
import org.koin.compose.koinInject

/** A crypto wallet that accepts donations. */
private data class DonationWallet(
    val name: String,
    val symbol: String,
    val address: String,
    @StringRes val network: Int,
    @StringRes val guide: Int,
    @StringRes val warning: Int,
    val accent: Color,
)

private val WALLETS = listOf(
    DonationWallet(
        name = "Tether / Tron",
        symbol = "TRX",
        address = "TEK8V2mfzFhj7S83zp3bgJEPTMj6D45qhZ",
        network = R.string.donate_trx_network,
        guide = R.string.donate_trx_guide,
        warning = R.string.donate_trx_warning,
        accent = Color(0xFFEB0029),
    ),
    DonationWallet(
        name = "Bitcoin",
        symbol = "BTC",
        address = "bc1qd6k2zap20pqxu2kj9ez9mhsl40d6acjd5nn79p",
        network = R.string.donate_btc_network,
        guide = R.string.donate_btc_guide,
        warning = R.string.donate_btc_warning,
        accent = Color(0xFFF7931A),
    ),
    DonationWallet(
        name = "Ethereum",
        symbol = "ETH",
        address = "0x1D1bBD5751625Dd505081Bd3ca8F755952c4eb09",
        network = R.string.donate_eth_network,
        guide = R.string.donate_eth_guide,
        warning = R.string.donate_eth_warning,
        accent = Color(0xFF627EEA),
    ),
    DonationWallet(
        name = "BNB",
        symbol = "BNB",
        address = "0x1D1bBD5751625Dd505081Bd3ca8F755952c4eb09",
        network = R.string.donate_bnb_network,
        guide = R.string.donate_bnb_guide,
        warning = R.string.donate_bnb_warning,
        accent = Color(0xFFF0B90B),
    ),
    DonationWallet(
        name = "Solana",
        symbol = "SOL",
        address = "DdzSTaEhdR5MT6YVfmkZCXfjZbLtNxDVb5qeZ6Uibps5",
        network = R.string.donate_sol_network,
        guide = R.string.donate_sol_guide,
        warning = R.string.donate_sol_warning,
        accent = Color(0xFF9945FF),
    ),
)

/** Iranian bank card, shown only when the app language is Persian. */
private const val IRAN_CARD_NUMBER = "6219861921832605"

@Composable
fun SettingDonatePage() {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current
    val isPersian = context.resources.configuration.locales[0].language == "fa"

    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(text = stringResource(R.string.donate_page_title)) },
                navigationIcon = { BackButton() },
                scrollBehavior = scrollBehavior,
                colors = CustomColors.topBarColors,
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = CustomColors.topBarColors.containerColor,
    ) { paddings ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = paddings.calculateTopPadding() + 8.dp,
                bottom = paddings.calculateBottomPadding() + 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item("hero") { DonateHero() }
            if (isPersian) {
                item("bankTitle") { SectionTitle(stringResource(R.string.donate_page_bank_title)) }
                item("bank") { BankCardItem() }
            }
            item("cryptoTitle") { SectionTitle(stringResource(R.string.donate_page_crypto_title)) }
            WALLETS.forEach { wallet ->
                item(wallet.symbol) { WalletCard(wallet) }
            }
            item("sponsorsTitle") { SectionTitle(stringResource(R.string.donate_page_sponsor_list)) }
            item("sponsors") { Sponsors() }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 4.dp),
    )
}

@Composable
private fun DonateHero() {
    val green = ultraGreen()
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(
                            green.copy(alpha = 0.28f),
                            MaterialTheme.colorScheme.primaryContainer,
                        )
                    )
                )
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(green),
                contentAlignment = Alignment.Center,
            ) {
                Icon(HugeIcons.InLove, contentDescription = null, tint = Color.White, modifier = Modifier.size(30.dp))
            }
            Text(
                text = stringResource(R.string.donate_page_hero_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                text = stringResource(R.string.donate_page_hero_body),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

/** Address / card number text that always reads left-to-right. */
@Composable
private fun LtrMonoText(text: String, big: Boolean = false) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        SelectionContainer {
            Text(
                text = text,
                fontFamily = FontFamily.Monospace,
                style = if (big) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyMedium,
                fontWeight = if (big) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                textAlign = TextAlign.Center,
            )
        }
    }
}

private fun copy(context: android.content.Context, text: String) {
    context.writeClipboardText(text)
    Toast.makeText(context, context.getString(R.string.donate_page_copied), Toast.LENGTH_SHORT).show()
}

@Composable
private fun WalletCard(wallet: DonationWallet) {
    val context = LocalContext.current
    var showQr by rememberSaveable(wallet.symbol) { mutableStateOf(false) }
    var showGuide by rememberSaveable(wallet.symbol + "_guide") { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(wallet.accent),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(wallet.symbol, color = Color.White, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(wallet.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = stringResource(R.string.donate_page_network, stringResource(wallet.network)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            LtrMonoText(wallet.address)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilledTonalButton(onClick = { copy(context, wallet.address) }) {
                    Icon(HugeIcons.Copy01, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text(stringResource(R.string.donate_page_copy_address), modifier = Modifier.padding(start = 6.dp))
                }
                OutlinedButton(onClick = { showQr = !showQr }) {
                    Text(stringResource(if (showQr) R.string.donate_page_hide_qr else R.string.donate_page_show_qr))
                }
                TextButton(onClick = { showGuide = !showGuide }) {
                    Text(stringResource(R.string.donate_page_how_to))
                }
            }
            AnimatedVisibility(showQr) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    QRCode(
                        value = wallet.address,
                        color = Color.Black,
                        backgroundColor = Color.White,
                        modifier = Modifier
                            .size(220.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .padding(8.dp),
                    )
                }
            }
            AnimatedVisibility(showGuide) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(wallet.guide), style = MaterialTheme.typography.bodyMedium)
                    WarningText(stringResource(wallet.warning))
                }
            }
        }
    }
}

@Composable
private fun WarningText(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(HugeIcons.Alert02, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.size(18.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
    }
}

@Composable
private fun BankCardItem() {
    val context = LocalContext.current
    val formatted = IRAN_CARD_NUMBER.chunked(4).joinToString(" ")
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ultraGreen()),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(HugeIcons.MoneyBag02, contentDescription = null, tint = Color.White)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.donate_page_bank_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = stringResource(R.string.donate_page_card_holder, stringResource(R.string.donate_page_card_holder_name)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            LtrMonoText(formatted, big = true)
            FilledTonalButton(onClick = { copy(context, IRAN_CARD_NUMBER) }) {
                Icon(HugeIcons.Copy01, contentDescription = null, modifier = Modifier.size(18.dp))
                Text(stringResource(R.string.donate_page_copy_card), modifier = Modifier.padding(start = 6.dp))
            }
            Text(stringResource(R.string.donate_card_guide), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun Sponsors() {
    val context = LocalContext.current
    val sponsorAPI = koinInject<SponsorAPI>()
    val sponsors by produceState<UiState<List<Sponsor>>>(UiState.Loading) {
        value = runCatching { UiState.Success(sponsorAPI.getSponsors()) }.getOrElse { UiState.Error(it) }
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            sponsors.onLoading {
                CircularWavyProgressIndicator()
            }.onError {
                SponsorsEmpty()
            }.onSuccess { list ->
                if (list.isEmpty()) {
                    SponsorsEmpty()
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        list.forEach { SponsorItem(it) }
                    }
                }
            }
            Text(
                text = stringResource(R.string.donate_page_sponsors_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            TextButton(onClick = { context.openUrl("${AppLinks.GITHUB}/issues") }) {
                Icon(HugeIcons.Github, contentDescription = null, modifier = Modifier.size(18.dp))
                Text(stringResource(R.string.donate_page_open_github), modifier = Modifier.padding(start = 6.dp))
            }
        }
    }
}

@Composable
private fun SponsorsEmpty() {
    Text(
        text = stringResource(R.string.donate_page_sponsors_empty),
        style = MaterialTheme.typography.titleSmall,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun SponsorItem(sponsor: Sponsor) {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.clickable(enabled = sponsor.url.isNotBlank()) { context.openUrl(sponsor.url) },
    ) {
        if (sponsor.avatar.isNotBlank()) {
            AsyncImage(
                model = sponsor.avatar,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
            )
        } else {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = sponsor.userName.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
        Text(sponsor.userName, style = MaterialTheme.typography.labelMedium, maxLines = 1)
        if (sponsor.amount.isNotBlank()) {
            Text(sponsor.amount, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        }
    }
}