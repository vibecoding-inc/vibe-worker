package me.profiluefter.vibeworker.mensa

data class MensaServiceRefreshResult(
    val oldMenus: List<MensaMenuDto>,
    val newMenus: List<MensaMenuDto>,
    val anyChanged: Boolean
)

interface MensaService {
    fun getMenus(): List<MensaMenuDto>
    fun refreshMenus(): MensaServiceRefreshResult
}
