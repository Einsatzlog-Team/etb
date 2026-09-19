package de.einsatzlog.app.ui.support

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.einsatzlog.app.support.SupportProduct
import de.einsatzlog.app.support.SupportTier
import de.einsatzlog.app.ui.components.atoms.SupporterBadge
import de.einsatzlog.app.ui.components.molecules.SupportTierCard
import de.einsatzlog.app.ui.theme.Spacing
import einsatzlog.ui.generated.resources.Res
import einsatzlog.ui.generated.resources.logbook_back
import einsatzlog.ui.generated.resources.support_intro_body
import einsatzlog.ui.generated.resources.support_intro_title
import einsatzlog.ui.generated.resources.support_payment_pending
import einsatzlog.ui.generated.resources.support_restore
import einsatzlog.ui.generated.resources.support_sub_note
import einsatzlog.ui.generated.resources.support_thanks_body
import einsatzlog.ui.generated.resources.support_thanks_title
import einsatzlog.ui.generated.resources.support_tier_large
import einsatzlog.ui.generated.resources.support_tier_medium
import einsatzlog.ui.generated.resources.support_tier_small
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
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(Spacing.m),
            verticalArrangement = Arrangement.spacedBy(Spacing.m),
        ) {
            when (val s = state) {
                SupportUiState.Loading -> Box(
                    modifier = Modifier.fillMaxWidth().padding(Spacing.xl),
                    contentAlignment = Alignment.Center,
                ) { CircularProgressIndicator() }

                SupportUiState.Unavailable -> Card {
                    Column(modifier = Modifier.padding(Spacing.m), verticalArrangement = Arrangement.spacedBy(Spacing.s)) {
                        Text(stringResource(Res.string.support_intro_title), style = MaterialTheme.typography.titleMedium)
                        Text(
                            stringResource(Res.string.support_unavailable),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                is SupportUiState.Loaded -> {
                    if (s.isSupporter) {
                        Card {
                            Column(
                                modifier = Modifier.padding(Spacing.m),
                                verticalArrangement = Arrangement.spacedBy(Spacing.s),
                            ) {
                                SupporterBadge()
                                Text(stringResource(Res.string.support_thanks_title), style = MaterialTheme.typography.titleMedium)
                                Text(
                                    stringResource(Res.string.support_thanks_body),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }
                    } else {
                        Card {
                            Column(
                                modifier = Modifier.padding(Spacing.m),
                                verticalArrangement = Arrangement.spacedBy(Spacing.s),
                            ) {
                                Text(stringResource(Res.string.support_intro_title), style = MaterialTheme.typography.titleMedium)
                                Text(
                                    stringResource(Res.string.support_intro_body),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }

                    s.error?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                    }
                    if (s.paymentPending) {
                        Text(
                            stringResource(Res.string.support_payment_pending),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    s.products.forEach { product ->
                        SupportTierCard(
                            title = tierTitle(product.tier),
                            subtitle = if (product.tier == SupportTier.SUBSCRIPTION) {
                                stringResource(Res.string.support_sub_note)
                            } else null,
                            priceLabel = product.localizedPrice,
                            busy = s.purchasing == product.tier,
                            onClick = { onPurchase(product.tier) },
                        )
                    }

                    TextButton(onClick = onRestore, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text(stringResource(Res.string.support_restore))
                    }
                }
            }
        }
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
