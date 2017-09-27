package camplotto.domain

import groovy.transform.AutoClone
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
@AutoClone
class ReservationDate {
    String name

    @Override
    String toString() {
        return name
    }
}

