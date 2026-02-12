package me.profiluefter.vibeworker.mensa.service

import me.profiluefter.vibeworker.mensa.MensaClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Component
class MensaClientImpl(
    @Qualifier(MENSA_REST_CLIENT_QUALIFIER) private val mensaRestClient: RestClient
) : MensaClient {

    override fun fetchMenus(): List<RestaurantMenuResponse> {
        val result = mensaRestClient.get()
            .uri(JkuEndpoints.MENUS)
            .retrieve()
            .body<List<RestaurantMenuResponse>>()
        return result ?: emptyList()
    }
}
