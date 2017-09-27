package camplotto.domain

import groovy.transform.AutoClone
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString
@EqualsAndHashCode
@AutoClone
class Registration {
    String name
    List<Site> preferredSites
    List<ReservationDate> preferredDates
    boolean preferSiteOverDate
    boolean hasPriority

}
