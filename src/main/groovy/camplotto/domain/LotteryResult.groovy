package camplotto.domain

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString
@EqualsAndHashCode
class LotteryResult {
    List<Reservation> reservations = []
    List<Registration> unmatchedRegistrations = []

    List<Reservation> getTakenReservations() {
        reservations.findAll { it.registration }
    }

    List<Reservation> getAvailableReservations() {
        reservations.findAll { !it.registration }
    }

    @Override
    String toString() {
        return "takenReservations:\n" + takenReservations.collect { "$it.registration.name / $it.site / $it.reservationDate\n"} +
                "\nunmatchedRegistrations:\n" + unmatchedRegistrations.collect { "$it.name\n" } + "\n"
    }
}
