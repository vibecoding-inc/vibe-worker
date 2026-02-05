package me.profiluefter.vibeworker.mensa

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

typealias MenuList = List<RestaurantMenuResponse>

object JkuEndpoints {
    const val BASE = "https://menu.jku.at"
    const val MENUS = "/api/menus"
}

@Component
class MensaClient(
    @Qualifier(MENSA_REST_CLIENT_QUALIFIER) private val mensaRestClient: RestClient
) {

    private val menuListType = object : ParameterizedTypeReference<MenuList>() {}

    fun downloadCurrentMenus(): MenuList {
        val result = mensaRestClient.get()
            .uri(JkuEndpoints.MENUS)
            .retrieve()
            .body(menuListType)
        return result ?: emptyList()
    }
}
