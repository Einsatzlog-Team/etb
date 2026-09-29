package de.einsatzlog.app.ui.support

import de.einsatzlog.app.support.PurchaseOutcome
import de.einsatzlog.app.support.SupportProduct
import de.einsatzlog.app.support.SupportRepository
import de.einsatzlog.app.support.SupportTier
import de.einsatzlog.app.support.SupporterStatus
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

private class FakeSupportRepository(
    override val isAvailable: Boolean = true,
    var products: List<SupportProduct> = emptyList(),
    var purchaseOutcome: PurchaseOutcome = PurchaseOutcome.Success,
) : SupportRepository {
    val status = MutableStateFlow(SupporterStatus(isSupporter = false))
    override val supporterStatus = status

    override suspend fun loadProducts(): Result<List<SupportProduct>> = Result.success(products)
    override suspend fun purchase(tier: SupportTier): PurchaseOutcome {
        if (purchaseOutcome is PurchaseOutcome.Success) status.value = SupporterStatus(true)
        return purchaseOutcome
    }
    override suspend fun restorePurchases(): Result<SupporterStatus> = Result.success(status.value)
}

@OptIn(ExperimentalCoroutinesApi::class)
class SupportViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private val demoProducts = listOf(
        SupportProduct(SupportTier.SMALL, "etb_support_small", "Kleine Unterstützung", "1,99 €"),
        SupportProduct(SupportTier.SUBSCRIPTION, "etb_supporter", "Unterstützer-Abo", "0,99 €"),
    )

    @Test
    fun unavailableRepository_showsUnavailable() = runTest(dispatcher.scheduler) {
        val vm = SupportViewModel(FakeSupportRepository(isAvailable = false))
        runCurrent()
        assertEquals(SupportUiState.Unavailable, vm.state.value)
    }

    @Test
    fun emptyProducts_showsUnavailable_notError() = runTest(dispatcher.scheduler) {
        val vm = SupportViewModel(FakeSupportRepository(products = emptyList()))
        runCurrent()
        assertEquals(SupportUiState.Unavailable, vm.state.value)
    }

    @Test
    fun successfulPurchase_activatesSupporter() = runTest(dispatcher.scheduler) {
        val repo = FakeSupportRepository(products = demoProducts)
        val vm = SupportViewModel(repo)
        runCurrent()

        vm.purchase(SupportTier.SMALL)
        runCurrent()

        val state = vm.state.value as SupportUiState.Loaded
        assertTrue(state.isSupporter)
        assertNull(state.error)
        assertNull(state.purchasing)
    }

    @Test
    fun cancelledPurchase_isSilent() = runTest(dispatcher.scheduler) {
        val repo = FakeSupportRepository(products = demoProducts, purchaseOutcome = PurchaseOutcome.Cancelled)
        val vm = SupportViewModel(repo)
        runCurrent()

        vm.purchase(SupportTier.SMALL)
        runCurrent()

        val state = vm.state.value as SupportUiState.Loaded
        assertNull(state.error, "cancel must not surface an error (spec 004)")
        assertNull(state.purchasing)
    }

    @Test
    fun recoverableError_surfacesMessage() = runTest(dispatcher.scheduler) {
        val repo = FakeSupportRepository(
            products = demoProducts,
            purchaseOutcome = PurchaseOutcome.Error("Netzwerkfehler", recoverable = true),
        )
        val vm = SupportViewModel(repo)
        runCurrent()

        vm.purchase(SupportTier.SMALL)
        runCurrent()

        assertEquals("Netzwerkfehler", (vm.state.value as SupportUiState.Loaded).error)
    }
}
