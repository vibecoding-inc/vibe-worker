package me.profiluefter.vibeworker.shibboleth.web

import me.profiluefter.vibeworker.shibboleth.ShibbolethService
import me.profiluefter.vibeworker.shibboleth.ShibbolethSession
import me.profiluefter.vibeworker.shibboleth.service.ShibbolethProperties
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.time.Instant

class ShibbolethDebugControllerTest {

    private val shibbolethService = mock(ShibbolethService::class.java)
    private val props = ShibbolethProperties()
    private val controller = ShibbolethDebugController(shibbolethService, props)

    @Test
    fun `testLogin returns success when service succeeds`() {
        val now = Instant.now()
        val session = ShibbolethSession(mapOf("test-cookie" to "value"), now)
        `when`(shibbolethService.login("https://moodle.jku.at/login/index.php")).thenReturn(session)

        val result = controller.testLogin(null)

        assertEquals("success", result["status"])
        assertEquals("https://moodle.jku.at/login/index.php", result["target"])
        assertEquals(now.toString(), result["expiresAt"])
        assertEquals(setOf("test-cookie"), result["cookiesObtained"])
    }

    @Test
    fun `testLogin returns error when service fails`() {
        `when`(shibbolethService.login("https://moodle.jku.at/login/index.php")).thenThrow(RuntimeException("Login failed"))

        val result = controller.testLogin(null)

        assertEquals("error", result["status"])
        assertEquals("Login failed", result["message"])
    }
}
