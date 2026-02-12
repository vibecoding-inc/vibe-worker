package me.profiluefter.vibeworker.mensa.service

import me.profiluefter.vibeworker.mensa.*
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class MensaPollingService(
    private val mensaService: MensaService,
    private val eventPublisher: ApplicationEventPublisher
) : ApplicationRunner {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun run(args: ApplicationArguments) {
        logger.info("Running initial menu poll on startup")
        pollMenus()
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    fun pollMenus() {
        logger.debug("Polling menus...")
        val result = mensaService.refreshMenus()

        if (result.anyChanged) {
            logger.info("Menus changed or new menus found, publishing MenuChangedEvent")
            eventPublisher.publishEvent(MenuChangedEvent(result.oldMenus, result.newMenus))
        }
    }
}
