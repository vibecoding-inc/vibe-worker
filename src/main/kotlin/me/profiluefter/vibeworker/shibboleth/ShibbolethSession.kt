package me.profiluefter.vibeworker.shibboleth

import java.time.Instant

/**
 * Represents a Shibboleth SSO session.
 *
 * @property cookies The cookies associated with the session.
 * @property expiresAt The expiration time of the session, if known.
 */
data class ShibbolethSession(
    val cookies: Map<String, String>,
    val expiresAt: Instant? = null
) {
    /**
     * Checks if the session is still valid.
     */
    fun isValid(): Boolean = expiresAt == null || expiresAt.isAfter(Instant.now())
}
