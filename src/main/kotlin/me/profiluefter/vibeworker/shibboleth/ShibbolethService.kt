package me.profiluefter.vibeworker.shibboleth

/**
 * Service for managing Shibboleth SSO sessions.
 */
interface ShibbolethService {
    /**
     * Provides a valid Shibboleth SSO session.
     * Implementations should handle caching and re-authentication if the session expires.
     *
     * @return A valid [ShibbolethSession].
     * @throws RuntimeException if authentication fails.
     */
    fun getSession(): ShibbolethSession
}
