package me.profiluefter.vibeworker.mensa.service

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MensaRestaurantRepository : JpaRepository<MensaRestaurant, Long>
