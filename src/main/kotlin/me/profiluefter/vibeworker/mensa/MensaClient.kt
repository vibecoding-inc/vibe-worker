package me.profiluefter.vibeworker.mensa

typealias MenuList = List<RestaurantMenuResponse>

interface MensaClient {
    /**
     * Downloads the current menus from the JKU Mensa API.
     */
    fun downloadCurrentMenus(): MenuList
}
