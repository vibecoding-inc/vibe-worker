package me.profiluefter.vibeworker.mensa

import org.junit.jupiter.api.Test
import org.springframework.web.client.RestClient
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MensaClientTest {

    /**
     * Integration test that fetches the current mensa menu from the real API.
     * This test verifies that the API is reachable and returns valid data that can be deserialized.
     */
    @Test
    fun `downloadCurrentMenus fetches real data without throwing`() {
        val restClient = RestClient.builder()
            .baseUrl(JkuEndpoints.BASE)
            .build()
        val mensaClient = MensaClient(restClient)

        val menus = mensaClient.downloadCurrentMenus()

        assertNotNull(menus)
        assertTrue(menus.isNotEmpty(), "Expected at least one restaurant menu")
        
        // Verify basic structure is present
        val firstMenu = menus.first()
        assertNotNull(firstMenu.restaurant)
        assertNotNull(firstMenu.restaurant.name)
        assertTrue(firstMenu.restaurant.name.isNotBlank(), "Restaurant name should not be blank")
    }

    /**
     * Test that verifies the mensa client can deserialize a known API response structure.
     */
    @Test
    fun `downloadCurrentMenus deserializes restaurant data correctly`() {
        val restClient = RestClient.builder()
            .baseUrl(JkuEndpoints.BASE)
            .build()
        val mensaClient = MensaClient(restClient)

        val menus = mensaClient.downloadCurrentMenus()

        // Find a known restaurant (JKU Mensa or Teichwerk should always exist)
        val knownRestaurants = menus.filter { 
            it.restaurant.name.contains("Mensa", ignoreCase = true) ||
            it.restaurant.name.contains("Teichwerk", ignoreCase = true) ||
            it.restaurant.name.contains("KHG", ignoreCase = true)
        }
        
        assertTrue(knownRestaurants.isNotEmpty(), "Expected to find at least one known JKU restaurant")
        
        // Verify restaurant has required fields populated
        knownRestaurants.forEach { menu ->
            assertTrue(menu.restaurant.id > 0, "Restaurant ID should be positive")
            assertNotNull(menu.restaurant.city)
        }
    }
}
