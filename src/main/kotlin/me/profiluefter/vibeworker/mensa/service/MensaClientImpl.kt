package me.profiluefter.vibeworker.mensa.service

import me.profiluefter.vibeworker.mensa.MensaClient
import me.profiluefter.vibeworker.mensa.MenuList
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class MensaClientImpl(
    @Qualifier(MENSA_REST_CLIENT_QUALIFIER) private val mensaRestClient: RestClient
) : MensaClient {

    private val menuListType = object : ParameterizedTypeReference<MenuList>() {}

    override fun downloadCurrentMenus(): MenuList {
        val result = mensaRestClient.get()
            .uri(JkuEndpoints.MENUS)
            .retrieve()
            .body(menuListType)
        return result ?: emptyList()
    }
}
