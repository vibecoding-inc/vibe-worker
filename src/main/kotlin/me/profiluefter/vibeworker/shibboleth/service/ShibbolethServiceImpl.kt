package me.profiluefter.vibeworker.shibboleth.service

import me.profiluefter.vibeworker.shibboleth.ShibbolethService
import me.profiluefter.vibeworker.shibboleth.ShibbolethSession
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.net.CookieManager
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
internal class ShibbolethServiceImpl(
    private val props: ShibbolethProperties
) : ShibbolethService {
    private val logger = LoggerFactory.getLogger(javaClass)
    private var cachedSession: ShibbolethSession? = null

    @Synchronized
    override fun getSession(): ShibbolethSession {
        val currentSession = cachedSession
        if (currentSession != null && currentSession.isValid()) {
            return currentSession
        }

        logger.info("Performing Shibboleth login for user {}", props.username)
        val session = performLogin()
        this.cachedSession = session
        return session
    }

    private fun performLogin(): ShibbolethSession {
        val cookieManager = CookieManager()
        val client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .cookieHandler(cookieManager)
            .build()

        // 1. Initial GET to obtain the login form and execution state
        val initialRequest = HttpRequest.newBuilder()
            .uri(URI.create(props.idpUrl))
            .GET()
            .build()

        val initialResponse = client.send(initialRequest, HttpResponse.BodyHandlers.ofString())

        if (initialResponse.statusCode() != 200) {
            throw RuntimeException("Failed to load Shibboleth login page: status ${initialResponse.statusCode()}")
        }

        val body = initialResponse.body()
        val execution =
            extractExecution(body) ?: throw RuntimeException("Could not find execution parameter in login page")

        // 2. POST credentials
        val formData = mapOf(
            "j_username" to (props.username ?: ""),
            "j_password" to (props.password ?: ""),
            "execution" to execution,
            "_eventId_proceed" to ""
        )

        val postBody = formData.entries.joinToString("&") {
            "${it.key}=${java.net.URLEncoder.encode(it.value, Charsets.UTF_8)}"
        }

        val loginRequest = HttpRequest.newBuilder()
            .uri(initialResponse.uri()) // Post to the same URL or the one we were redirected to
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(postBody))
            .build()

        val loginResponse = client.send(loginRequest, HttpResponse.BodyHandlers.ofString())

        if (loginResponse.statusCode() != 200 || loginResponse.body().contains("login-error")) {
            logger.error(
                "Shibboleth login failed for user {}. Response body length: {}",
                props.username,
                loginResponse.body().length
            )
            throw RuntimeException("Shibboleth login failed. Please check credentials.")
        }

        // 3. Extract cookies
        val idpUri = URI.create(props.idpUrl)
        val cookies = cookieManager.cookieStore.get(idpUri).associate {
            it.name to it.value
        }

        if (cookies.isEmpty()) {
            throw RuntimeException("No cookies received after Shibboleth login")
        }

        logger.info("Successfully obtained Shibboleth session for user {}", props.username)

        // Shibboleth sessions usually last a few hours. We'll set a conservative expiration of 2 hours.
        return ShibbolethSession(
            cookies = cookies,
            expiresAt = Instant.now().plus(2, ChronoUnit.HOURS)
        )
    }

    private fun extractExecution(html: String): String? {
        val regex = Regex("""name="execution" value="([^"]+)"""")
        return regex.find(html)?.groupValues?.get(1)
    }
}
