package camplotto.domain

import groovy.transform.AutoClone
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString
@EqualsAndHashCode
@AutoClone
class Reservation {
    Registration registration
    ReservationDate reservationDate
    Site site
}
