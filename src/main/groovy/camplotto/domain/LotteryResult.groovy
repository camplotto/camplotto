package camplotto.domain

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString
@EqualsAndHashCode
@CompileStatic
class LotteryResult {
    List<Reservation> availableReservations
    List<Reservation> takenReservations = []
    List<Registration> unmatchedRegistrations = []
}
