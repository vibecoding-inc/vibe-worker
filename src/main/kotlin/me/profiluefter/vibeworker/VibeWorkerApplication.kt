package me.profiluefter.vibeworker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class VibeWorkerApplication

fun main(args: Array<String>) {
    runApplication<VibeWorkerApplication>(*args)
}
