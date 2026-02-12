package me.profiluefter.vibeworker.mensa.service

import me.profiluefter.vibeworker.mensa.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.context.ApplicationEventPublisher
import java.time.LocalDate

class MensaPollingServiceTest {

    private val mensaService = mock(MensaService::class.java)
    private val eventPublisher = mock(ApplicationEventPublisher::class.java)
    private val pollingService = MensaPollingService(mensaService, eventPublisher)

    @Test
    fun `pollMenus publishes event when menus change`() {
        val oldMenus = emptyList<MensaMenuDto>()
        val newMenus = listOf(MensaMenuDto(1, "Test", LocalDate.now(), emptyList()))

        `when`(mensaService.refreshMenus()).thenReturn(MensaServiceRefreshResult(oldMenus, newMenus, true))

        // First poll - should publish event
        pollingService.pollMenus()
        verify(eventPublisher).publishEvent(MenuChangedEvent(oldMenus, newMenus))

        // Second poll - no change reported by service
        reset(eventPublisher)
        `when`(mensaService.refreshMenus()).thenReturn(MensaServiceRefreshResult(newMenus, newMenus, false))
        pollingService.pollMenus()
        verifyNoInteractions(eventPublisher)
    }

    @Test
    fun `run calls pollMenus`() {
        `when`(mensaService.refreshMenus()).thenReturn(MensaServiceRefreshResult(emptyList(), emptyList(), false))

        pollingService.run(mock(org.springframework.boot.ApplicationArguments::class.java))

        verify(mensaService).refreshMenus()
    }
}
