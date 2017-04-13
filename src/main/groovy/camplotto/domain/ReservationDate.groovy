package camplotto.domain

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import java.time.LocalDate

@ToString
@EqualsAndHashCode
@CompileStatic
class ReservationDate {
    Season season
    String name
    LocalDate startDate
    LocalDate throughDate
}
