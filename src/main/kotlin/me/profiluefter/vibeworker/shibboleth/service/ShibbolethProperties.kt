package me.profiluefter.vibeworker.shibboleth.service

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("shibboleth")
internal data class ShibbolethProperties(
    val idpUrl: String = "https://shibboleth.im.jku.at/idp/profile/SAML2/Redirect/SSO",
    val username: String? = null,
    val password: String? = null
)
