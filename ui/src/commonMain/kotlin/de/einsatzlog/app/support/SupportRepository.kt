package de.einsatzlog.app.support

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

data class SupporterStatus(val isSupporter: Boolean)

enum class SupportTier { SMALL, MEDIUM, LARGE, SUBSCRIPTION }

data class SupportProduct(
    val tier: SupportTier,
    val id: String,
    val title: String,
    val localizedPrice: String,
)

sealed interface PurchaseOutcome {
    data object Success : PurchaseOutcome
    data object Cancelled : PurchaseOutcome
    data class Error(val message: String, val recoverable: Boolean) : PurchaseOutcome
}

/**
 * Seam for the supporter/donation monetization (spec 004). The RevenueCat
 * implementation arrives with slice 3 in the `store` build; `foss` keeps [NoopSupportRepository].
 */
interface SupportRepository {
    val isAvailable: Boolean
    val supporterStatus: Flow<SupporterStatus>
    suspend fun loadProducts(): Result<List<SupportProduct>>
    suspend fun purchase(tier: SupportTier): PurchaseOutcome
    suspend fun restorePurchases(): Result<SupporterStatus>
}

class NoopSupportRepository : SupportRepository {
    override val isAvailable: Boolean = false
    override val supporterStatus: Flow<SupporterStatus> =
        MutableStateFlow(SupporterStatus(isSupporter = false))

    override suspend fun loadProducts(): Result<List<SupportProduct>> = Result.success(emptyList())
    override suspend fun purchase(tier: SupportTier): PurchaseOutcome =
        PurchaseOutcome.Error("Not available in this build", recoverable = false)

    override suspend fun restorePurchases(): Result<SupporterStatus> =
        Result.success(SupporterStatus(isSupporter = false))
}
