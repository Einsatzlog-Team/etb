package de.einsatzlog.app.ui.support

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.einsatzlog.app.support.SupportProduct
import de.einsatzlog.app.support.SupportTier
import de.einsatzlog.app.ui.components.atoms.SupporterBadge
import de.einsatzlog.app.ui.components.molecules.SupportTierCard
import de.einsatzlog.app.ui.theme.Spacing
import einsatzlog.ui.generated.resources.Res
import einsatzlog.ui.generated.resources.logbook_back
import einsatzlog.ui.generated.resources.support_again
import einsatzlog.ui.generated.resources.support_choose
import einsatzlog.ui.generated.resources.support_intro_body
import einsatzlog.ui.generated.resources.support_intro_title
import einsatzlog.ui.generated.resources.support_payment_pending
import einsatzlog.ui.generated.resources.support_promise
import einsatzlog.ui.generated.resources.support_restore
import einsatzlog.ui.generated.resources.support_sub_note
import einsatzlog.ui.generated.resources.support_thanks_body
import einsatzlog.ui.generated.resources.support_thanks_title
import einsatzlog.ui.generated.resources.support_tier_large
import einsatzlog.ui.generated.resources.support_tier_large_note
import einsatzlog.ui.generated.resources.support_tier_medium
import einsatzlog.ui.generated.resources.support_tier_medium_note
import einsatzlog.ui.generated.resources.support_tier_small
import einsatzlog.ui.generated.resources.support_tier_small_note
import einsatzlog.ui.generated.resources.support_tier_sub
import einsatzlog.ui.generated.resources.support_title
import einsatzlog.ui.generated.resources.support_unavailable
import org.jetbrains.compose.resources.stringResource

sealed interface SupportUiState {
    data object Loading : SupportUiState
    data object Unavailable : SupportUiState
    data class Loaded(
        val products: List<SupportProduct>,
        val isSupporter: Boolean,
        val purchasing: SupportTier? = null,
        val error: String? = null,
        val paymentPending: Boolean = false,
    ) : SupportUiState
}

// Same gold as SupporterBadge — the supporter accent, used nowhere else.
private val SupporterGold = Color(0xFFF5C518)
private val OnSupporterGold = Color(0xFF3F2E00)

/** "Unterstützen" (spec 004, Flow 4): never blocks, never nags. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportContent(
    state: SupportUiState,
    onPurchase: (SupportTier) -> Unit,
    onRestore: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.support_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.logbook_back),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            val isSupporter = (state as? SupportUiState.Loaded)?.isSupporter == true
            SupportHero(isSupporter = isSupporter)

            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.m),
                verticalArrangement = Arrangement.spacedBy(Spacing.m),
            ) {
                when (val s = state) {
                    SupportUiState.Loading -> Box(
                        modifier = Modifier.fillMaxWidth().padding(Spacing.xl),
                        contentAlignment = Alignment.Center,
                    ) { CircularProgressIndicator() }

                    SupportUiState.Unavailable -> Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    ) {
                        Text(
                            stringResource(Res.string.support_unavailable),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(Spacing.m),
                        )
                    }

                    is SupportUiState.Loaded -> LoadedSection(s, onPurchase, onRestore)
                }
            }
            Spacer(Modifier.height(Spacing.l))
        }
    }
}

@Composable
private fun SupportHero(isSupporter: Boolean) {
    val accent = MaterialTheme.colorScheme.primaryContainer
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(accent.copy(alpha = 0.35f), Color.Transparent)))
            .padding(horizontal = Spacing.l, vertical = Spacing.l),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.s),
    ) {
        if (isSupporter) {
            HeroIcon(Icons.Filled.Star, container = SupporterGold, tint = OnSupporterGold)
            SupporterBadge()
            HeroText(
                title = stringResource(Res.string.support_thanks_title),
                body = stringResource(Res.string.support_thanks_body),
            )
        } else {
            HeroIcon(
                Icons.Filled.Favorite,
                container = MaterialTheme.colorScheme.primaryContainer,
                tint = MaterialTheme.colorScheme.primary,
            )
            HeroText(
                title = stringResource(Res.string.support_intro_title),
                body = stringResource(Res.string.support_intro_body),
            )
        }
    }
}

@Composable
private fun HeroIcon(icon: ImageVector, container: Color, tint: Color) {
    Surface(shape = CircleShape, color = container, modifier = Modifier.size(80.dp)) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(40.dp))
        }
    }
}

@Composable
private fun HeroText(title: String, body: String) {
    Text(
        title,
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = Spacing.s),
    )
    Text(
        body,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun LoadedSection(
    s: SupportUiState.Loaded,
    onPurchase: (SupportTier) -> Unit,
    onRestore: () -> Unit,
) {
    s.error?.let {
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
            Text(
                it,
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(Spacing.m),
            )
        }
    }
    if (s.paymentPending) {
        Text(
            stringResource(Res.string.support_payment_pending),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }

    Text(
        stringResource(if (s.isSupporter) Res.string.support_again else Res.string.support_choose),
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
    )

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.s + Spacing.xs)) {
        s.products.forEach { product ->
            SupportTierCard(
                title = tierTitle(product.tier),
                subtitle = tierNote(product.tier),
                priceLabel = product.localizedPrice,
                busy = s.purchasing == product.tier,
                hearts = tierHearts(product.tier),
                onClick = { onPurchase(product.tier) },
            )
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(top = Spacing.s),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp),
        )
        Text(
            stringResource(Res.string.support_promise),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }

    TextButton(onClick = onRestore, modifier = Modifier.fillMaxWidth()) {
        Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.size(Spacing.s))
        Text(stringResource(Res.string.support_restore))
    }
}

@Composable
private fun tierTitle(tier: SupportTier): String = stringResource(
    when (tier) {
        SupportTier.SMALL -> Res.string.support_tier_small
        SupportTier.MEDIUM -> Res.string.support_tier_medium
        SupportTier.LARGE -> Res.string.support_tier_large
        SupportTier.SUBSCRIPTION -> Res.string.support_tier_sub
    }
)

@Composable
private fun tierNote(tier: SupportTier): String = stringResource(
    when (tier) {
        SupportTier.SMALL -> Res.string.support_tier_small_note
        SupportTier.MEDIUM -> Res.string.support_tier_medium_note
        SupportTier.LARGE -> Res.string.support_tier_large_note
        SupportTier.SUBSCRIPTION -> Res.string.support_sub_note
    }
)

private fun tierHearts(tier: SupportTier): Int = when (tier) {
    SupportTier.SMALL, SupportTier.SUBSCRIPTION -> 1
    SupportTier.MEDIUM -> 2
    SupportTier.LARGE -> 3
}
