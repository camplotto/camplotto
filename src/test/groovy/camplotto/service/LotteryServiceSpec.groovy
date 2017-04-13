package camplotto.service

import camplotto.domain.Registration
import spock.lang.Specification

class LotteryServiceSpec extends Specification {

    def "ShuffleAndPrioritize"() {
        given:
        Registration nonPrioritizedRegistration = new Registration(hasPriority: false)
        Registration prioritizedRegistration = new Registration(hasPriority: true)
        List<Registration> registrations = [nonPrioritizedRegistration, prioritizedRegistration]

        when:
        LotteryService.shuffleAndPrioritize(registrations)

        then:
        registrations[0] == prioritizedRegistration
        registrations[1] == nonPrioritizedRegistration

    }
}
