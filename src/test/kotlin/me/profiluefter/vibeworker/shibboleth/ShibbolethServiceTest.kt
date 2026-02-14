package me.profiluefter.vibeworker.shibboleth

import me.profiluefter.vibeworker.shibboleth.service.ShibbolethProperties
import me.profiluefter.vibeworker.shibboleth.service.ShibbolethServiceImpl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ShibbolethServiceTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var shibbolethService: ShibbolethService

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val props = ShibbolethProperties(
            defaultTargetUrl = mockWebServer.url("/target").toString(),
            username = "testuser",
            password = "testpassword"
        )
        shibbolethService = ShibbolethServiceImpl(props)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `login should perform login and return cookies`() {
        // 1. Initial GET response with execution ID
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""<html><body><input name="execution" value="e1s1"></body></html>""")
        )

        // 2. POST login response with cookies
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .addHeader("Set-Cookie", "shib_idp_session=abc123def; Path=/; HttpOnly")
                .setBody("Success")
        )

        val session = shibbolethService.login(mockWebServer.url("/target").toString())

        assertTrue(session.isValid())
        assertEquals("abc123def", session.cookies["shib_idp_session"])

        // Verify requests
        val initialRequest = mockWebServer.takeRequest()
        assertEquals("GET", initialRequest.method)
        assertEquals("/target", initialRequest.path)

        val loginRequest = mockWebServer.takeRequest()
        assertEquals("POST", loginRequest.method)
        val body = loginRequest.body.readUtf8()
        assertTrue(body.contains("j_username=testuser"))
        assertTrue(body.contains("j_password=testpassword"))
        assertTrue(body.contains("execution=e1s1"))
    }

    @Test
    fun `login should not cache the session`() {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody("""<input name="execution" value="e1s1">""")
        )
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).addHeader("Set-Cookie", "session=1").setBody("OK")
        )
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody("""<input name="execution" value="e1s2">""")
        )
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).addHeader("Set-Cookie", "session=2").setBody("OK")
        )

        val target = mockWebServer.url("/target").toString()
        val session1 = shibbolethService.login(target)
        val session2 = shibbolethService.login(target)

        assertTrue(session1 != session2)
        assertEquals(4, mockWebServer.requestCount)
    }
}
