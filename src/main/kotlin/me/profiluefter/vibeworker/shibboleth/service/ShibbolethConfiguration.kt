package me.profiluefter.vibeworker.shibboleth.service

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(ShibbolethProperties::class)
internal class ShibbolethConfiguration
