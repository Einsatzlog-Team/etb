package de.einsatzlog.app.ui.support

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.revenuecat.purchases.kmp.models.CustomerInfo
import com.revenuecat.purchases.kmp.models.StoreTransaction
import com.revenuecat.purchases.kmp.ui.revenuecatui.Paywall
import com.revenuecat.purchases.kmp.ui.revenuecatui.PaywallListener
import com.revenuecat.purchases.kmp.ui.revenuecatui.PaywallOptions
import org.koin.compose.viewmodel.koinViewModel

/**
 * ViewModel wrapper (spec 004). Non-supporters with a loaded offering get the
 * RevenueCat Paywall designed in the dashboard (offering `default`); loading,
 * thank-you and unavailable (foss / placeholder keys) stay on [SupportContent],
 * so the SDK UI is never touched when RevenueCat isn't configured.
 */
@Composable
fun SupportScreen(
    onBack: () -> Unit,
    viewModel: SupportViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val current = state
    if (current is SupportUiState.Loaded && !current.isSupporter) {
        SupportPaywall(onDismiss = onBack, onEntitlementChanged = viewModel::load)
    } else {
        SupportContent(
            state = state,
            onPurchase = viewModel::purchase,
            onRestore = viewModel::restore,
            onBack = onBack,
        )
    }
}

@Composable
private fun SupportPaywall(onDismiss: () -> Unit, onEntitlementChanged: () -> Unit) {
    val options = remember(onDismiss, onEntitlementChanged) {
        PaywallOptions(dismissRequest = onDismiss) {
            shouldDisplayDismissButton = true
            listener = object : PaywallListener {
                // Reload → the `supporter` entitlement flips the screen to the thank-you state.
                override fun onPurchaseCompleted(customerInfo: CustomerInfo, storeTransaction: StoreTransaction) =
                    onEntitlementChanged()

                override fun onRestoreCompleted(customerInfo: CustomerInfo) = onEntitlementChanged()
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
        Paywall(options)
    }
}
