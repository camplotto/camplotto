package camplotto.service

import camplotto.domain.*
import spock.lang.Specification
import spock.lang.Unroll

class LotteryServiceSpec extends Specification {

    final Site siteACap1 = new Site(name: 'a', capacity: 1)
    final Site siteBCap2 = new Site(name: 'b', capacity: 2)

    final ReservationDate week1 = new ReservationDate(name: 'week 1')
    final ReservationDate week2 = new ReservationDate(name: 'week 2')

    final Reservation week1SiteA = new Reservation(reservationDate: week1, site: siteACap1)
    final Reservation week2SiteA = new Reservation(reservationDate: week2, site: siteACap1)
    final Reservation week1SiteB = new Reservation(reservationDate: week1, site: siteBCap2)
    final Reservation week2SiteB = new Reservation(reservationDate: week2, site: siteBCap2)

    def "ShuffleAndPrioritize"() {
        given:
        Registration nonPrioritizedRegistration = new Registration(hasPriority: false)
        Registration prioritizedRegistration = new Registration(hasPriority: true)
        Registration nonPrioritizedRegistration2 = new Registration(hasPriority: false)

        List<Registration> registrations = [nonPrioritizedRegistration, prioritizedRegistration, nonPrioritizedRegistration2]

        when:
        LotteryService.shuffleAndPrioritize(registrations)

        then:
        registrations.size() == 3
        registrations[0] == prioritizedRegistration
    }

    def "getBestResult when a result has more taken reservations"() {
        given:
        LotteryResult a = new LotteryResult(takenReservations: [new Reservation(), new Reservation()])
        LotteryResult b = new LotteryResult(takenReservations: [new Reservation()])

        expect:
        LotteryService.getBestResult(a, b) == a
    }

    def "findAvailableReservation match based on preference"() {
        given:
        List<Reservation> availableReservations = [week2SiteA, week1SiteB]

        when:
        Registration registration = new Registration(preferredSites: [siteACap1, siteBCap2], preferredDates: [week1, week2], preferSiteOverDate: true)

        then:
        LotteryService.findAvailableReservation(availableReservations, registration) == week2SiteA

        when:
        registration.preferSiteOverDate = false

        then:
        LotteryService.findAvailableReservation(availableReservations, registration) == week1SiteB
    }

    def "findAvailableReservation no match"() {
        given:
        List<Reservation> availableReservations = [week1SiteA]

        when:
        Registration registration = new Registration(preferredSites: [siteBCap2], preferredDates: [week2])

        then:
        !LotteryService.findAvailableReservation(availableReservations, registration)
    }

    def "lottery should result in site A remaining for a single week because not enough people wanted it"() {
        given:
        List<Reservation> availableReservations = [week1SiteA, week1SiteB, week2SiteA, week2SiteB]
        Registration reg1 = new Registration(preferredSites: [siteBCap2], preferredDates: [week1, week2])
        Registration reg2 = new Registration(preferredSites: [siteBCap2], preferredDates: [week1, week2])
        Registration reg3 = new Registration(preferredSites: [siteACap1], preferredDates: [week1, week2])

        when:
        LotteryResult lotteryResult = LotteryService.run(availableReservations, [reg1, reg2, reg3])

        then:
        lotteryResult.availableReservations.size() == 1
        lotteryResult.availableReservations[0].site == siteACap1
        lotteryResult.takenReservations.size() == 3
    }

    @Unroll
    def "the best lottery scenario is having one site left, with either registration 2 or 3 being unmatched"() {
        given:
        List<Reservation> availableReservations = [week1SiteA, week1SiteB]
        Registration reg1 = new Registration(preferredSites: [siteACap1, siteBCap2], preferredDates: [week1])
        Registration reg2 = new Registration(preferredSites: [siteACap1], preferredDates: [week1])
        Registration reg3 = new Registration(preferredSites: [siteACap1], preferredDates: [week1])

        when:
        LotteryResult lotteryResult = LotteryService.run(availableReservations, [reg1, reg2, reg3])

        then:
        lotteryResult.availableReservations.size() == 0
        lotteryResult.takenReservations.size() == 2
        lotteryResult.unmatchedRegistrations.size() == 1
        lotteryResult.unmatchedRegistrations[0] in [reg2, reg3]

        where: "running this test multiple times should always produce the best possible result"
        a << [1..10]
    }

    @Unroll
    def "reg2 always get registered because they have priority, even though they'd normally sometimes not get in"() {
        given:
        List<Reservation> availableReservations = [week1SiteA, week1SiteB]
        Registration reg1 = new Registration(preferredSites: [siteACap1, siteBCap2], preferredDates: [week1])
        Registration reg2 = new Registration(preferredSites: [siteACap1], preferredDates: [week1], hasPriority: true)
        Registration reg3 = new Registration(preferredSites: [siteACap1], preferredDates: [week1])

        when:
        LotteryResult lotteryResult = LotteryService.run(availableReservations, [reg1, reg2, reg3])

        then:
        lotteryResult.availableReservations.size() == 0
        lotteryResult.takenReservations.size() == 2
        lotteryResult.unmatchedRegistrations.size() == 1
        lotteryResult.unmatchedRegistrations[0] == reg3

        where: "running this test multiple times should always produce the best possible result"
        a << [1..10]
    }

}
