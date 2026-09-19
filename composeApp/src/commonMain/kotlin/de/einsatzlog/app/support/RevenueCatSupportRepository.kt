package de.einsatzlog.app.support

import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.ktx.awaitCustomerInfo
import com.revenuecat.purchases.kmp.ktx.awaitOfferings
import com.revenuecat.purchases.kmp.ktx.awaitPurchase
import com.revenuecat.purchases.kmp.ktx.awaitRestore
import com.revenuecat.purchases.kmp.models.CustomerInfo
import com.revenuecat.purchases.kmp.models.Package
import com.revenuecat.purchases.kmp.models.PurchasesErrorCode
import com.revenuecat.purchases.kmp.models.PurchasesTransactionException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * SupportRepository backed by RevenueCat (spec 004). Selected by DI only in
 * the `store` flavor with a configured key — everything else gets the Noop.
 */
class RevenueCatSupportRepository : SupportRepository {

    private companion object {
        const val ENTITLEMENT_SUPPORTER = "supporter"
        const val PKG_SMALL = "support_small"
        const val PKG_MEDIUM = "support_medium"
        const val PKG_LARGE = "support_large"
    }

    override val isAvailable: Boolean = true

    private val _supporterStatus = MutableStateFlow(SupporterStatus(isSupporter = false))
    override val supporterStatus: Flow<SupporterStatus> = _supporterStatus

    private var packagesByTier: Map<SupportTier, Package> = emptyMap()

    override suspend fun loadProducts(): Result<List<SupportProduct>> = runCatching {
        refreshStatus()
        val offering = Purchases.sharedInstance.awaitOfferings().current
            ?: return@runCatching emptyList()

        packagesByTier = buildMap {
            offering.availablePackages.forEach { pkg ->
                tierFor(pkg)?.let { put(it, pkg) }
            }
        }

        packagesByTier.entries
            .sortedBy { it.key.ordinal }
            .map { (tier, pkg) ->
                SupportProduct(
                    tier = tier,
                    id = pkg.storeProduct.id,
                    title = pkg.storeProduct.title,
                    localizedPrice = pkg.storeProduct.price.formatted,
                )
            }
    }

    override suspend fun purchase(tier: SupportTier): PurchaseOutcome {
        val pkg = packagesByTier[tier]
            ?: return PurchaseOutcome.Error("Product not loaded", recoverable = true)
        return try {
            Purchases.sharedInstance.awaitPurchase(packageToPurchase = pkg)
            refreshStatus()
            PurchaseOutcome.Success
        } catch (e: PurchasesTransactionException) {
            when {
                e.userCancelled || e.error.code == PurchasesErrorCode.PurchaseCancelledError ->
                    PurchaseOutcome.Cancelled
                e.error.code == PurchasesErrorCode.PaymentPendingError ->
                    PurchaseOutcome.Error("Zahlung ausstehend", recoverable = true)
                e.error.code == PurchasesErrorCode.NetworkError ||
                    e.error.code == PurchasesErrorCode.OfflineConnectionError ||
                    e.error.code == PurchasesErrorCode.StoreProblemError ->
                    PurchaseOutcome.Error(e.error.message, recoverable = true)
                else -> PurchaseOutcome.Error(e.error.message, recoverable = false)
            }
        }
    }

    override suspend fun restorePurchases(): Result<SupporterStatus> = runCatching {
        toStatus(Purchases.sharedInstance.awaitRestore()).also { _supporterStatus.value = it }
    }

    private suspend fun refreshStatus() {
        runCatching {
            _supporterStatus.value = toStatus(Purchases.sharedInstance.awaitCustomerInfo())
        }
    }

    private fun toStatus(info: CustomerInfo) =
        SupporterStatus(isSupporter = info.entitlements.active.containsKey(ENTITLEMENT_SUPPORTER))

    private fun tierFor(pkg: Package): SupportTier? = when (pkg.identifier) {
        PKG_SMALL -> SupportTier.SMALL
        PKG_MEDIUM -> SupportTier.MEDIUM
        PKG_LARGE -> SupportTier.LARGE
        "\$rc_monthly" -> SupportTier.SUBSCRIPTION
        else -> null
    }
}
