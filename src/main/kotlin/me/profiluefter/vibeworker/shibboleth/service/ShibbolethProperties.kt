package me.profiluefter.vibeworker.shibboleth.service

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("shibboleth")
internal data class ShibbolethProperties(
    val defaultTargetUrl: String = "https://moodle.jku.at/login/index.php",
    val username: String? = null,
    val password: String? = null
)
