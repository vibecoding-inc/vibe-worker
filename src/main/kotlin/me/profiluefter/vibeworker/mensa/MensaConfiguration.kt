package me.profiluefter.vibeworker.mensa

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

const val MENSA_REST_CLIENT_QUALIFIER = "mensaRestClient"

@Configuration
class MensaConfiguration {

    @Bean
    @Qualifier(MENSA_REST_CLIENT_QUALIFIER)
    fun mensaRestClient(): RestClient = RestClient.builder()
        .baseUrl(JkuEndpoints.BASE)
        .build()
}
