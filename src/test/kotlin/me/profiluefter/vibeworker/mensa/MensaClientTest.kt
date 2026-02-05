package me.profiluefter.vibeworker.mensa

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestClient
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MensaClientTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var mockMensaResponse: String

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        mockMensaResponse = javaClass.getResource("/me/profiluefter/vibeworker/mensa/mock-mensa-response.json")!!.readText()
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    /**
     * Integration test that fetches the current mensa menu from the real API.
     * Only checks that deserialization works without errors.
     */
    @Test
    fun `downloadCurrentMenus deserializes real API response without errors`() {
        val restClient = RestClient.builder()
            .baseUrl(JkuEndpoints.BASE)
            .build()
        val mensaClient = MensaClient(restClient)

        // Just verify it doesn't throw - deserialization works
        val menus = mensaClient.downloadCurrentMenus()
        assertNotNull(menus)
    }

    /**
     * Test with mock data that verifies the mensa client correctly deserializes restaurant data.
     */
    @Test
    fun `downloadCurrentMenus deserializes mocked restaurant data correctly`() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(mockMensaResponse)
        )

        val restClient = RestClient.builder()
            .baseUrl(mockWebServer.url("/").toString().trimEnd('/'))
            .build()
        val mensaClient = MensaClient(restClient)

        val menus = mensaClient.downloadCurrentMenus()

        assertEquals(2, menus.size)
        
        // Verify first restaurant (JKU Mensa)
        val jkuMensa = menus.find { it.restaurant.name == "JKU Mensa" }
        assertNotNull(jkuMensa)
        assertEquals(4L, jkuMensa.restaurant.id)
        assertEquals("Linz", jkuMensa.restaurant.city)
        assertEquals("4040", jkuMensa.restaurant.postalCode)
        assertTrue(jkuMensa.restaurant.toGo)
        
        // Verify second restaurant (Teichwerk)
        val teichwerk = menus.find { it.restaurant.name == "Teichwerk" }
        assertNotNull(teichwerk)
        assertEquals(3L, teichwerk.restaurant.id)
        assertTrue(teichwerk.restaurant.preOrdering)
    }

    /**
     * Test that verifies opening hours are correctly deserialized from mock data.
     */
    @Test
    fun `downloadCurrentMenus deserializes opening hours correctly`() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(mockMensaResponse)
        )

        val restClient = RestClient.builder()
            .baseUrl(mockWebServer.url("/").toString().trimEnd('/'))
            .build()
        val mensaClient = MensaClient(restClient)

        val menus = mensaClient.downloadCurrentMenus()
        val jkuMensa = menus.find { it.restaurant.name == "JKU Mensa" }
        assertNotNull(jkuMensa)

        val openingHours = jkuMensa.restaurant.openingHours
        assertTrue(openingHours.isNotEmpty())
        
        val mondayHours = openingHours.find { it.openingDay == java.time.DayOfWeek.MONDAY }
        assertNotNull(mondayHours)
        assertEquals(false, mondayHours.closed)
        assertTrue(mondayHours.openingHours.isNotEmpty())
        assertEquals("11:00", mondayHours.openingHours.first().openFrom)
        assertEquals("14:00", mondayHours.openingHours.first().openTo)
    }

    /**
     * Test that verifies menu types are correctly deserialized from mock data.
     */
    @Test
    fun `downloadCurrentMenus deserializes menu types correctly`() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(mockMensaResponse)
        )

        val restClient = RestClient.builder()
            .baseUrl(mockWebServer.url("/").toString().trimEnd('/'))
            .build()
        val mensaClient = MensaClient(restClient)

        val menus = mensaClient.downloadCurrentMenus()
        val jkuMensa = menus.find { it.restaurant.name == "JKU Mensa" }
        assertNotNull(jkuMensa)

        val menuTypes = jkuMensa.menuTypes
        assertTrue(menuTypes.isNotEmpty())
        
        val classic1 = menuTypes.find { it.menuTypeName == "Classic 1" }
        assertNotNull(classic1)
        assertNotNull(classic1.menu)
        assertEquals(1.0, classic1.menu?.sortOrder)
    }

    /**
     * Test that empty response is handled correctly.
     */
    @Test
    fun `downloadCurrentMenus returns empty list for empty response`() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("[]")
        )

        val restClient = RestClient.builder()
            .baseUrl(mockWebServer.url("/").toString().trimEnd('/'))
            .build()
        val mensaClient = MensaClient(restClient)

        val menus = mensaClient.downloadCurrentMenus()
        assertTrue(menus.isEmpty())
    }
}
