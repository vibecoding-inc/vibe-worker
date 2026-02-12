package me.profiluefter.vibeworker.mensa.service

import me.profiluefter.vibeworker.mensa.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.time.LocalDate

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
