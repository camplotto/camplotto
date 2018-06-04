package camplotto

class SingleRegistrationService {

    fun handleRegistration(currentResult: LotteryResult, registration: Registration) {
        val reservation = findAvailableSingleRegistration(currentResult.getAvailableReservations(), registration)
        if (reservation != null) {
            reservation.registration = registration
        } else {
            currentResult.unmatchedRegistrations.add(registration)
        }
    }

    fun findAvailableSingleRegistration(availableReservations: List<Reservation>, registration: Registration): Reservation? {

        // maybe todo: shuffle the availableReservations sites and weeks to prevent front-loading reservations?
        val matchingReservations = availableReservations.filter { it.site in registration.preferredSites && it.reservationDate in registration.preferredDates }

        if (registration.preferSiteOverDate) {
            registration.preferredSites.forEach { preferredSite ->
                val reservation = matchingReservations.find { it.site == preferredSite && it.reservationDate in registration.preferredDates }
                if (reservation != null) return reservation
            }
        } else {
            registration.preferredDates.forEach { preferredDate ->
                val reservation = matchingReservations.find { it.reservationDate == preferredDate && it.site in registration.preferredSites }
                if (reservation != null) return reservation
            }
        }
        return null
    }

}
