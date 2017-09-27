package camplotto.domain

import groovy.transform.AutoClone
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString
@EqualsAndHashCode
@AutoClone
class Site {
    String name
    int capacity
}
