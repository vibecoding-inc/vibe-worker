package me.profiluefter.vibeworker.shibboleth.web

import me.profiluefter.vibeworker.shibboleth.ShibbolethService
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/debug/shibboleth")
@Profile("local")
internal class ShibbolethDebugController(
    private val shibbolethService: ShibbolethService,
    private val props: me.profiluefter.vibeworker.shibboleth.service.ShibbolethProperties
) {

    @GetMapping("/test")
    fun testLogin(@RequestParam(required = false) target: String?): Map<String, Any> {
        val targetUrl = target ?: props.defaultTargetUrl
        return try {
            val session = shibbolethService.login(targetUrl)
            mapOf(
                "status" to "success",
                "target" to targetUrl,
                "expiresAt" to session.expiresAt.toString(),
                "cookiesObtained" to session.cookies.keys
            )
        } catch (e: Exception) {
            mapOf(
                "status" to "error",
                "message" to (e.message ?: "Unknown error")
            )
        }
    }
}
