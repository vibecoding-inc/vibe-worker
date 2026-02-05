package me.profiluefter.vibeworker

import org.junit.jupiter.api.Test
import org.springframework.modulith.core.ApplicationModules
import org.springframework.modulith.docs.Documenter

class ModulithTest {

    private val modules = ApplicationModules.of(VibeWorkerApplication::class.java)

    @Test
    fun `verify module structure`() {
        modules.verify()
    }

    @Test
    fun `write documentation`() {
        Documenter(modules).writeModulesAsPlantUml()
    }
}
