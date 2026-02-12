package me.profiluefter.vibeworker.mensa.service

import me.profiluefter.vibeworker.mensa.MensaClient
import me.profiluefter.vibeworker.mensa.MenuChangedEvent
import me.profiluefter.vibeworker.mensa.Restaurant
import me.profiluefter.vibeworker.mensa.RestaurantMenuResponse
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.context.ApplicationEventPublisher

class MensaPollingServiceTest {

    private val mensaClient = mock(MensaClient::class.java)
    private val eventPublisher = mock(ApplicationEventPublisher::class.java)
    private val pollingService = MensaPollingService(mensaClient, eventPublisher)

    @Test
    fun `pollMenus publishes event when menus change`() {
        val restaurant = Restaurant(
            1, "Test", null, null, null, null, null, null, null, null, 
            false, null, false, null, null, null, emptyList()
        )
        val menus1 = listOf(RestaurantMenuResponse(restaurant, emptyList(), null, null))
        val menus2 = listOf(RestaurantMenuResponse(restaurant.copy(name = "Changed"), emptyList(), null, null))

        `when`(mensaClient.downloadCurrentMenus()).thenReturn(menus1)

        // First poll - should publish event as it's the first fetch
        pollingService.pollMenus()
        verify(eventPublisher).publishEvent(MenuChangedEvent(menus1))

        // Second poll - no change
        reset(eventPublisher)
        `when`(mensaClient.downloadCurrentMenus()).thenReturn(menus1)
        pollingService.pollMenus()
        verifyNoInteractions(eventPublisher)

        // Third poll - change
        `when`(mensaClient.downloadCurrentMenus()).thenReturn(menus2)
        pollingService.pollMenus()
        verify(eventPublisher).publishEvent(MenuChangedEvent(menus2))
    }
}
