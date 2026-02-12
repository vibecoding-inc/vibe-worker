package me.profiluefter.vibeworker.mensa.service

import me.profiluefter.vibeworker.mensa.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class MensaServiceImpl(
    private val mensaClient: MensaClient,
    private val menuRepository: MensaMenuRepository,
    private val restaurantRepository: MensaRestaurantRepository
) : MensaService {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun getMenus(): List<MensaMenuDto> {
        val today = LocalDate.now()
        return menuRepository.findAll()
            .filter { it.menuDate == today }
            .map { it.toDto() }
    }

    @Transactional
    override fun refreshMenus(): MensaServiceRefreshResult {
        val oldMenus = getMenus()
        val apiMenus = mensaClient.fetchMenus()
        val result = persistMenus(apiMenus)
        return MensaServiceRefreshResult(oldMenus, result.menus, result.anyChanged)
    }

    private data class PersistResult(val menus: List<MensaMenuDto>, val anyChanged: Boolean)

    private fun persistMenus(menus: List<RestaurantMenuResponse>): PersistResult {
        val today = LocalDate.now()
        var anyChanged = false
        val menuDtos = mutableListOf<MensaMenuDto>()

        for (restaurantMenu in menus) {
            val restaurantDto = restaurantMenu.restaurant
            val restaurant = restaurantRepository.findById(restaurantDto.id).orElseGet {
                restaurantRepository.save(MensaRestaurant(id = restaurantDto.id, name = restaurantDto.name))
            }

            val apiDishes = mutableListOf<Dish>()
            restaurantMenu.menu?.restaurantMenuGroups?.flatMap { it.dishes }?.let { apiDishes.addAll(it) }
            restaurantMenu.menuTypes.mapNotNull { it.menu?.groupedDishes?.values?.flatten() }.forEach { apiDishes.addAll(it) }

            val newDishDtos = apiDishes.map { it.toMensaDto() }.distinct()

            val existingMenu = menuRepository.findByRestaurantAndMenuDate(restaurant, today)
            val menu = if (existingMenu != null) {
                val existingDishes = existingMenu.dishes
                val existingDishDtos = existingDishes.map { it.toDto() }.sortedBy { it.name }
                val sortedNewDishDtos = newDishDtos.sortedBy { it.name }
                
                if (existingDishDtos != sortedNewDishDtos) {
                    logger.warn("Menu for restaurant {} ({}) on {} changed during refresh!", restaurant.name, restaurant.id, today)
                    anyChanged = true
                    
                    // Smart update: Remove dishes not in new list
                    existingDishes.removeIf { existingDish ->
                        newDishDtos.none { it.name == existingDish.name && it.category == existingDish.category }
                    }
                    
                    // Update or Add
                    newDishDtos.forEach { newDto ->
                        val existingDish = existingDishes.find { it.name == newDto.name && it.category == newDto.category }
                        if (existingDish != null) {
                            // Update price
                            existingDish.price = newDto.price
                        } else {
                            // Add new
                            existingDishes.add(MensaDish(
                                name = newDto.name,
                                price = newDto.price,
                                category = newDto.category,
                                menu = existingMenu
                            ))
                        }
                    }
                    menuRepository.save(existingMenu)
                }
                existingMenu
            } else {
                anyChanged = true
                val newMenu = MensaMenu(
                    restaurant = restaurant,
                    menuDate = today
                )
                newDishDtos.forEach { dto ->
                    newMenu.dishes.add(MensaDish(
                        name = dto.name,
                        price = dto.price,
                        category = dto.category,
                        menu = newMenu
                    ))
                }
                menuRepository.save(newMenu)
            }
            menuDtos.add(menu.toDto())
        }

        return PersistResult(menuDtos, anyChanged)
    }

    private fun Dish.toMensaDto() = MensaDishDto(
        name = name,
        price = prices.firstOrNull()?.price,
        category = category?.name
    )

    private fun MensaMenu.toDto() = MensaMenuDto(
        restaurantId = restaurant.id,
        restaurantName = restaurant.name,
        date = menuDate,
        dishes = dishes.map { it.toDto() }
    )

    private fun MensaDish.toDto() = MensaDishDto(
        name = name,
        price = price,
        category = category
    )
}
