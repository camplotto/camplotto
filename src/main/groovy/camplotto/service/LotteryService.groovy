package camplotto.service

import camplotto.domain.LotteryResult
import camplotto.domain.Registration
import camplotto.domain.Reservation

class LotteryService {


    static LotteryResult run(List<Reservation> availableReservations, List<Registration> registrations) {
        LotteryResult bestResult = new LotteryResult()

        for (int i = 0; i < 10; i++) {
            LotteryResult currentResult = new LotteryResult(availableReservations: availableReservations.clone() as List<Reservation>)

            shuffleAndPrioritize(registrations)

            registrations.each { Registration registration ->
                // todo handle preferSiteOverDate and ordered ranking of preferences
                Reservation reservation = currentResult.availableReservations.find {
                    it.site in registration.preferredSites && it.reservationDate in registration.preferredDates
                }

                if (reservation) {
                    reservation.registration = registration
                    currentResult.availableReservations.remove(reservation)
                    currentResult.takenReservations << reservation
                } else {
                    currentResult.unmatchedRegistrations << registration
                }
            }

            bestResult = getBetterResult(currentResult, bestResult)
        }

        return bestResult
    }

    static List<Registration> shuffleAndPrioritize(List<Registration> registrations) {
        Collections.shuffle(registrations)
        return registrations.sort { a, b -> b.hasPriority <=> a.hasPriority }
    }

    static LotteryResult getBetterResult(LotteryResult a, LotteryResult b) {
        if (a.takenReservations.size() > b.takenReservations.size()) {
            return a
        }

        if (a.availableReservations.sum { it.site.capacity } < b.availableReservations.sum { it.site.capacity }) {
            return a
        }

        return b
    }

}
