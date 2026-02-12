package me.profiluefter.vibeworker.mensa

import me.profiluefter.vibeworker.mensa.service.RestaurantMenuResponse

interface MensaClient {
    /**
     * Fetches fresh menus from the API.
     */
    fun fetchMenus(): List<RestaurantMenuResponse>
}
