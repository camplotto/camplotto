package camplotto.domain

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString
@EqualsAndHashCode
@CompileStatic
class Registration {
    String name
    List<Site> preferredSites
    List<ReservationDate> preferredDates
    boolean preferSiteOverDate
    boolean hasPriority

}
