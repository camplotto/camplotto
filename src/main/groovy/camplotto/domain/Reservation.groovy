package camplotto.domain

import groovy.transform.AutoClone
import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString
@EqualsAndHashCode
@CompileStatic
@AutoClone
class Reservation {
    Registration registration
    ReservationDate reservationDate
    Site site
}
