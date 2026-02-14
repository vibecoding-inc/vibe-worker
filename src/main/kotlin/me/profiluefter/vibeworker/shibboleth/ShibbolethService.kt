package me.profiluefter.vibeworker.shibboleth

/**
 * Service for managing Shibboleth SSO sessions.
 */
interface ShibbolethService {
    /**
     * Performs a Shibboleth login for the specified target URL.
     *
     * @param targetUrl The URL of the service to log into (e.g., moodle.jku.at/login/index.php).
     * @return A [ShibbolethSession] containing the cookies obtained during the login process.
     * @throws RuntimeException if authentication fails.
     */
    fun login(targetUrl: String): ShibbolethSession
}
