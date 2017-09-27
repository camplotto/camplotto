package camplotto.service

import camplotto.domain.*

class LotteryService {

    protected static final LOTTERY_SIMULATION_COUNT = 1000

    static LotteryResult run(List<Reservation> reservations, List<Registration> registrations) {
        LotteryResult bestResult = new LotteryResult()

        for (int i = 0; i < LOTTERY_SIMULATION_COUNT; i++) {

            LotteryResult currentResult = new LotteryResult(reservations: reservations.collect { it.clone() } as List<Reservation>, unmatchedRegistrations: [])

            List<Registration> registrationsCopy = registrations.collect { it.clone() }
            shuffleAndPrioritize(registrationsCopy)

            registrationsCopy.each { Registration registration ->
                Reservation reservation = findAvailableReservation(currentResult.availableReservations, registration)
                if (reservation) {
                    reservation.registration = registration
                } else {
                    currentResult.unmatchedRegistrations << registration
                }
            }

            bestResult = getBestResult(currentResult, bestResult)
        }
        return bestResult
    }

    static Reservation findAvailableReservation(List<Reservation> availableReservations, Registration registration) {

        // initial filter.  this can be tweaked further, e.g. validate capacity, optimize capacity
        List<Reservation> matchingReservations = availableReservations.findAll { Reservation reservation ->
            reservation.site in registration.preferredSites && reservation.reservationDate in registration.preferredDates
        }

        if (registration.preferSiteOverDate) {
            for (Site preferredSite : registration.preferredSites) {
                Reservation reservation = matchingReservations.find { it.site == preferredSite && it.reservationDate in registration.preferredDates }
                if (reservation) {
                    return reservation
                }
            }
        } else {
            for (ReservationDate preferredDate : registration.preferredDates) {
                Reservation reservation = matchingReservations.find { it.reservationDate == preferredDate && it.site in registration.preferredSites }
                if (reservation) {
                    return reservation
                }
            }
        }

        return null
    }

    static void shuffleAndPrioritize(List<Registration> registrations) {
        Collections.shuffle(registrations)
        registrations.sort { a, b -> b.hasPriority <=> a.hasPriority }
    }

    static LotteryResult getBestResult(LotteryResult a, LotteryResult b) {
        if (a.takenReservations.size() > b.takenReservations.size()) {
            return a
        }

        return b
    }

}
