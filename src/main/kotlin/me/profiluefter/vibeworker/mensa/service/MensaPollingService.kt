package me.profiluefter.vibeworker.mensa.service

import me.profiluefter.vibeworker.mensa.MensaClient
import me.profiluefter.vibeworker.mensa.MenuChangedEvent
import me.profiluefter.vibeworker.mensa.MenuList
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MensaPollingService(
    private val mensaClient: MensaClient,
    private val eventPublisher: ApplicationEventPublisher
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private var lastMenus: MenuList? = null

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    fun pollMenus() {
        logger.debug("Polling menus...")
        val currentMenus = mensaClient.downloadCurrentMenus()

        if (currentMenus != lastMenus) {
            logger.info("Menus changed, publishing MenuChangedEvent")
            lastMenus = currentMenus
            eventPublisher.publishEvent(MenuChangedEvent(currentMenus))
        }
    }
}
