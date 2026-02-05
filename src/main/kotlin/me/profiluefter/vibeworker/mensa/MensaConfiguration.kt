package me.profiluefter.vibeworker.mensa

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class MensaConfiguration {

    @Bean
    fun mensaRestClient(): RestClient = RestClient.builder()
        .baseUrl(JkuEndpoints.BASE)
        .build()
}
