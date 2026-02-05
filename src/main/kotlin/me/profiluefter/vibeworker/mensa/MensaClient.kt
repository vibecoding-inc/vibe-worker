package me.profiluefter.vibeworker.mensa

import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.core.ParameterizedTypeReference

typealias MenuList = List<RestaurantMenuResponse>

object JkuEndpoints {
    const val BASE = "https://menu.jku.at"
    const val MENUS = "/api/menus"
}

class MenuMatcher(private val data: MenuList) {
    
    fun byVenueName(n: String): RestaurantMenuResponse? {
        val target = n.lowercase()
        var idx = 0
        while (idx < data.size) {
            val current = data[idx]
            if (current.restaurant.name.lowercase() == target) {
                return current
            }
            idx++
        }
        return null
    }
    
    fun byVenueId(i: Long): RestaurantMenuResponse? {
        var idx = 0
        while (idx < data.size) {
            val current = data[idx]
            if (current.restaurant.id == i) {
                return current
            }
            idx++
        }
        return null
    }
}

@Component
class MensaClient(b: RestClient.Builder) {
    
    private val api = b.baseUrl(JkuEndpoints.BASE).build()
    
    private val menuListType = object : ParameterizedTypeReference<MenuList>() {}
    
    fun downloadCurrentMenus(): MenuList {
        val result = api.get()
            .uri(JkuEndpoints.MENUS)
            .retrieve()
            .body(menuListType)
        return result ?: emptyList()
    }
    
    fun matcher(): MenuMatcher = MenuMatcher(downloadCurrentMenus())
}
