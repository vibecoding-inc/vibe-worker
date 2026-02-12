package me.profiluefter.vibeworker.mensa

import org.springframework.modulith.events.Externalized

@Externalized("mensa.menu-changed")
data class MenuChangedEvent(
    val menus: MenuList
)
