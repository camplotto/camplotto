package camplotto

class LotteryService {

    fun run(availableReservations: List<Reservation>, registrations: List<Registration>, numSimulations: Int = 1000): LotteryResult {

        var bestResult = LotteryResult()

        for (i in 1..numSimulations) {
            val currentResult = LotteryResult(reservations = availableReservations.map { it.copy() }, unmatchedRegistrations = mutableListOf())

            val currentRegistrations = registrations.map { it.copy() }.shuffleAndPrioritize()

            currentRegistrations.forEach { registration ->
                val reservation = findAvailableReservation(currentResult.getAvailableReservations(), registration)
                if (reservation != null) {
                    reservation.registration = registration
                } else {
                    currentResult.unmatchedRegistrations.add(registration)
                }
            }

            bestResult = getBestResult(currentResult, bestResult)
        }

        return bestResult
    }

    fun findAvailableReservation(availableReservations: List<Reservation>, registration: Registration): Reservation? {

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

    fun getBestResult(a: LotteryResult, b: LotteryResult): LotteryResult {
        return if (a.getTakenReservations().size > b.getTakenReservations().size) a else b
    }
}
