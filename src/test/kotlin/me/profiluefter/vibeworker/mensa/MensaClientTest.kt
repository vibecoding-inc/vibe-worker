package me.profiluefter.vibeworker.mensa

import me.profiluefter.vibeworker.mensa.service.*
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito.*
import org.springframework.web.client.RestClient
import java.time.LocalDate
import java.util.*
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
     * Test that fetchMenus returns data from API.
     */
    @Test
    fun `fetchMenus returns data from API`() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(mockMensaResponse)
        )

        val restClient = RestClient.builder()
            .baseUrl(mockWebServer.url("/").toString().trimEnd('/'))
            .build()
        val mensaClient = MensaClientImpl(restClient)

        val result = mensaClient.fetchMenus()

        assertTrue(result.isNotEmpty())
        assertEquals("JKU Mensa", result[0].restaurant.name)
        assertEquals(4L, result[0].restaurant.id)
    }

    /**
     * Test that empty response is handled correctly.
     */
    @Test
    fun `fetchMenus returns empty list for empty response`() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("[]")
        )

        val restClient = RestClient.builder()
            .baseUrl(mockWebServer.url("/").toString().trimEnd('/'))
            .build()
        val mensaClient = MensaClientImpl(restClient)

        val result = mensaClient.fetchMenus()
        assertTrue(result.isEmpty())
    }
}
