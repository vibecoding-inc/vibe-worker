package me.profiluefter.vibeworker.shibboleth.web

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@Profile("local")
internal class ShibbolethDebugSecurityConfiguration {

    @Bean
    fun debugSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .securityMatcher("/debug/**")
            .authorizeHttpRequests { auth ->
                auth.anyRequest().permitAll()
            }
            .csrf { it.disable() }

        return http.build()
    }
}
