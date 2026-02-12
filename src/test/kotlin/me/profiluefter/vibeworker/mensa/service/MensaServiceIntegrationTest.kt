package me.profiluefter.vibeworker.mensa.service

import me.profiluefter.vibeworker.TestcontainersConfiguration
import me.profiluefter.vibeworker.mensa.MensaClient
import me.profiluefter.vibeworker.mensa.MensaDishDto
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest
@Import(TestcontainersConfiguration::class)
@Transactional
class MensaServiceIntegrationTest {

    @Autowired
    private lateinit var mensaService: MensaServiceImpl

    @MockitoBean
    private lateinit var mensaClient: MensaClient

    @Autowired
    private lateinit var menuRepository: MensaMenuRepository

    @Autowired
    private lateinit var restaurantRepository: MensaRestaurantRepository

    @Autowired
    private lateinit var entityManager: jakarta.persistence.EntityManager

    @Test
    fun `refreshMenus performs upsert and detects changes`() {
        // Clear repositories before test to ensure clean state
        menuRepository.deleteAll()
        restaurantRepository.deleteAll()
        entityManager.flush()
        entityManager.clear()

        val restaurantDto = Restaurant(
            id = 4L,
            name = "JKU Mensa",
            street = null, streetNr = null, postalCode = null, city = null, phoneNr = null, email = null,
            imageUrl = null, homePageLink = null, toGo = false, toGoInfo = null, preOrdering = false,
            menuOpeningTimesDe = null, menuOpeningTimesEn = null, preOrderingInfo = null, openingHours = emptyList()
        )
        
        val dish1 = Dish(
            id = 1, name = "Pasta", type = null, category = DishCategory.MAIN_COURSE, sortOrder = 1.0, sides = null,
            kosher = false, halal = false, alternative = null, prices = listOf(Price(1, 5.0, "Price", 1.0)),
            allergens = emptyList(), dishTemplateId = 1
        )

        // API Response without nested menu for restaurant groups (similar to how JKU often returns)
        val apiResponse1 = listOf(
            RestaurantMenuResponse(
                restaurant = restaurantDto,
                menuTypes = listOf(
                    MenuType(
                        menuTypeName = "Standard",
                        menu = TypedMenu(
                            id = 1, sortOrder = 1.0, prices = emptyList(), description = null,
                            menuTypeName = "Standard", date = null, menuTypeId = 1,
                            priceRequirement = "None",
                            groupedDishes = mapOf("MAIN_COURSE" to listOf(dish1))
                        )
                    )
                ),
                menu = RestaurantMenu(id = 1, active = true, restaurantMenuGroups = emptyList()),
                closedReason = null
            )
        )

        `when`(mensaClient.fetchMenus()).thenReturn(apiResponse1)

        // First refresh - initial save
        val result1 = mensaService.refreshMenus()
        entityManager.flush()
        entityManager.clear()
        
        assertTrue(result1.anyChanged)
        assertEquals(1, result1.newMenus.size)
        assertEquals("Pasta", result1.newMenus[0].dishes[0].name)
        assertEquals(1, menuRepository.count())

        // Second refresh - no change (Mock exactly same response)
        `when`(mensaClient.fetchMenus()).thenReturn(apiResponse1)
        val result2 = mensaService.refreshMenus()
        entityManager.flush()
        entityManager.clear()
        
        assertTrue(!result2.anyChanged, "Should not be changed on second refresh")
        assertEquals(1, menuRepository.count())

        // Third refresh - change (new dish)
        val dish2 = dish1.copy(name = "Pizza")
        val apiResponse2 = listOf(
            apiResponse1[0].copy(
                menuTypes = listOf(
                    apiResponse1[0].menuTypes[0].copy(
                        menu = apiResponse1[0].menuTypes[0].menu?.copy(
                            groupedDishes = mapOf("MAIN_COURSE" to listOf(dish2))
                        )
                    )
                )
            )
        )
        `when`(mensaClient.fetchMenus()).thenReturn(apiResponse2)

        val result3 = mensaService.refreshMenus()
        entityManager.flush()
        entityManager.clear()
        
        assertTrue(result3.anyChanged)
        assertEquals("Pizza", result3.newMenus[0].dishes[0].name)
        assertEquals("Pasta", result3.oldMenus[0].dishes[0].name)
        assertEquals(1, menuRepository.count()) // Still only 1 menu record for today
    }
}
