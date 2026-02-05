package me.profiluefter.vibeworker.mensa

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * DTOs for the JKU Mensa Menu API response.
 * These classes map directly to the JSON structure from https://menu.jku.at/api/menus
 */

@JsonIgnoreProperties(ignoreUnknown = true)
data class RestaurantMenuResponse(
    val restaurant: Restaurant,
    val menuTypes: List<MenuType>,
    val menu: RestaurantMenu?,
    val closedReason: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Restaurant(
    val id: Long,
    val name: String,
    val street: String?,
    val streetNr: String?,
    val postalCode: String?,
    val city: String?,
    val phoneNr: String?,
    val email: String?,
    val imageUrl: String?,
    val homePageLink: String?,
    val toGo: Boolean,
    val toGoInfo: String?,
    val preOrdering: Boolean,
    val menuOpeningTimesDe: String?,
    val menuOpeningTimesEn: String?,
    val preOrderingInfo: String?,
    val openingHours: List<OpeningDay>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class OpeningDay(
    val id: Long,
    val openingDay: DayOfWeek,
    val closed: Boolean,
    val openingHours: List<OpeningHoursSlot>
)

enum class DayOfWeek {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class OpeningHoursSlot(
    val id: Long,
    val openFrom: String,
    val openTo: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class RestaurantMenu(
    val id: Long,
    val active: Boolean,
    val restaurantMenuGroups: List<RestaurantMenuGroup>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class RestaurantMenuGroup(
    val id: Long,
    val name: String,
    val sortOrder: Double,
    val dishes: List<Dish>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MenuType(
    val menuTypeName: String,
    val menu: TypedMenu?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TypedMenu(
    val id: Long,
    val sortOrder: Double?,
    val prices: List<Price>,
    val description: String?,
    val menuTypeName: String?,
    val date: String?,
    val menuTypeId: Long?,
    val priceRequirement: String?,
    val groupedDishes: Map<String, List<Dish>>?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Dish(
    val id: Long,
    val name: String,
    val type: DishType?,
    val category: DishCategory?,
    val sortOrder: Double,
    val sides: String?,
    val kosher: Boolean,
    val halal: Boolean,
    val alternative: String?,
    val prices: List<Price>,
    val allergens: List<Allergen>,
    val dishTemplateId: Long?
)

enum class DishType {
    VEGAN, VEGETARIAN, MEAT, FISH
}

enum class DishCategory {
    STARTER, MAIN_COURSE, DESSERT
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class Price(
    val id: Long,
    val price: Double,
    val description: String?,
    val sortOrder: Double
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Allergen(
    val id: Long,
    val name: String,
    val shortName: String
)
