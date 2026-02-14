package me.profiluefter.vibeworker.shibboleth

import me.profiluefter.vibeworker.TestcontainersConfiguration
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import kotlin.test.assertTrue

@SpringBootTest
@Import(TestcontainersConfiguration::class)
@EnabledIfEnvironmentVariable(named = "SHIBBOLETH_USER", matches = ".+")
class ShibbolethSmokeTest {

    @Autowired
    private lateinit var shibbolethService: ShibbolethService

    @Test
    fun `should successfully login to real IdP`() {
        val session = shibbolethService.login("https://moodle.jku.at/login/index.php")

        println("Successfully obtained cookies: ${session.cookies.keys}")
        assertTrue(session.isValid(), "Session should be valid")
        assertTrue(session.cookies.isNotEmpty(), "Should have obtained cookies")
    }
}
