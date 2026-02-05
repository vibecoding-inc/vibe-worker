package me.profiluefter.vibeworker.mensa

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class MenuMatcherTest {

    private val sampleMenuList: MenuList = listOf(
        RestaurantMenuResponse(
            restaurant = Restaurant(
                id = 1L,
                name = "Test Mensa",
                street = "Test Street",
                streetNr = "1",
                postalCode = "4040",
                city = "Linz",
                phoneNr = "0732 123456",
                email = "test@example.com",
                imageUrl = null,
                homePageLink = null,
                toGo = true,
                toGoInfo = null,
                preOrdering = false,
                menuOpeningTimesDe = null,
                menuOpeningTimesEn = null,
                preOrderingInfo = null,
                openingHours = emptyList()
            ),
            menuTypes = emptyList(),
            menu = null,
            closedReason = null
        ),
        RestaurantMenuResponse(
            restaurant = Restaurant(
                id = 2L,
                name = "Another Restaurant",
                street = "Another Street",
                streetNr = "2",
                postalCode = "4040",
                city = "Linz",
                phoneNr = "0732 654321",
                email = "another@example.com",
                imageUrl = null,
                homePageLink = null,
                toGo = false,
                toGoInfo = null,
                preOrdering = true,
                menuOpeningTimesDe = null,
                menuOpeningTimesEn = null,
                preOrderingInfo = null,
                openingHours = emptyList()
            ),
            menuTypes = emptyList(),
            menu = null,
            closedReason = null
        )
    )

    @Test
    fun `byVenueName returns matching restaurant when found`() {
        val matcher = MenuMatcher(sampleMenuList)

        val result = matcher.byVenueName("Test Mensa")

        assertNotNull(result)
        assertEquals(1L, result.restaurant.id)
        assertEquals("Test Mensa", result.restaurant.name)
    }

    @Test
    fun `byVenueName is case insensitive`() {
        val matcher = MenuMatcher(sampleMenuList)

        val result = matcher.byVenueName("TEST MENSA")

        assertNotNull(result)
        assertEquals(1L, result.restaurant.id)
    }

    @Test
    fun `byVenueName returns null when not found`() {
        val matcher = MenuMatcher(sampleMenuList)

        val result = matcher.byVenueName("Non Existent")

        assertNull(result)
    }

    @Test
    fun `byVenueId returns matching restaurant when found`() {
        val matcher = MenuMatcher(sampleMenuList)

        val result = matcher.byVenueId(2L)

        assertNotNull(result)
        assertEquals("Another Restaurant", result.restaurant.name)
    }

    @Test
    fun `byVenueId returns null when not found`() {
        val matcher = MenuMatcher(sampleMenuList)

        val result = matcher.byVenueId(999L)

        assertNull(result)
    }

    @Test
    fun `matcher handles empty list`() {
        val matcher = MenuMatcher(emptyList())

        assertNull(matcher.byVenueName("Any"))
        assertNull(matcher.byVenueId(1L))
    }
}
