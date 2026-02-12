package me.profiluefter.vibeworker.mensa.service

import jakarta.persistence.*
import java.time.Instant
import java.time.LocalDate

@Entity
class MensaRestaurant(
    @Id
    val id: Long,
    val name: String,

    @OneToMany(mappedBy = "restaurant")
    val menus: MutableList<MensaMenu> = mutableListOf()
)

@Entity
class MensaMenu(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "restaurant_id")
    val restaurant: MensaRestaurant,
    
    val menuDate: LocalDate,
    val fetchedAt: Instant = Instant.now(),

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "menu")
    val dishes: MutableList<MensaDish> = mutableListOf()
)

@Entity
class MensaDish(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    val name: String,
    var price: Double?,
    val category: String?,

    @ManyToOne
    @JoinColumn(name = "menu_id")
    var menu: MensaMenu? = null
)
