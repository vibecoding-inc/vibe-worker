package me.profiluefter.vibeworker.mensa.service

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface MensaMenuRepository : JpaRepository<MensaMenu, Int> {
    fun deleteByRestaurantAndMenuDate(restaurant: MensaRestaurant, menuDate: LocalDate)
    fun findByRestaurantAndMenuDate(restaurant: MensaRestaurant, menuDate: LocalDate): MensaMenu?
    fun saveAndFlush(menu: MensaMenu): MensaMenu
}
