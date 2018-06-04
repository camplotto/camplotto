package camplotto

import java.util.*

data class ReservationDate(val name: String)

data class Site(val name: String)

data class Registration(
        val name: String,
        val group: String? = null,
        val preferredSites: List<Site>,
        val preferredDates: List<ReservationDate>,
        val preferSiteOverDate: Boolean,
        val priorityRank: Int = Int.MAX_VALUE,
        var wasGroupProcessed: Boolean = false) {

    fun isPartOfGroup(): Boolean = !group.isNullOrEmpty()
}

fun List<Registration>.shuffleAndPrioritize(): List<Registration> {
    Collections.shuffle(this)
    return this.sortedBy { it.priorityRank }
}

fun List<Registration>.getGroupMembers(registration: Registration): List<Registration> {
    return this.filter { it.group == registration.group }
}

data class Reservation(
        var registration: Registration? = null,
        val reservationDate: ReservationDate,
        val site: Site)

data class LotteryResult(
        val reservations: List<Reservation> = emptyList(),
        val unmatchedRegistrations: MutableList<Registration> = mutableListOf()) {

    fun getTakenReservations(): List<Reservation> {
        return reservations.filter { it.registration != null }
    }

    fun getAvailableReservations(): List<Reservation> {
        return reservations.filter { it.registration == null }
    }
}

