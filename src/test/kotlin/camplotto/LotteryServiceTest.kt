package camplotto

import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test


class LotteryServiceTest {

    private val siteA = Site(name = "a")
    private val siteB = Site(name = "b")

    private val week1 = ReservationDate(name = "week 1")
    private val week2 = ReservationDate(name = "week 2")

    private val week1SiteA = Reservation(reservationDate = week1, site = siteA)
    private val week2SiteA = Reservation(reservationDate = week2, site = siteA)
    private val week1SiteB = Reservation(reservationDate = week1, site = siteB)
    private val week2SiteB = Reservation(reservationDate = week2, site = siteB)

    private val defaultRegistration = Registration(name = "registration", preferredSites = emptyList(), preferredDates = emptyList(), preferSiteOverDate = true)

    @Test
    fun shuffleAndPrioritize() {
        val nonPrioritizedRegistration1 = defaultRegistration.copy(name = "1")
        val prioritizedRegistration = defaultRegistration.copy(name = "prioritized", priorityRank = 1)
        val nonPrioritizedRegistration2 = defaultRegistration.copy(name = "2")

        val registrations = listOf(nonPrioritizedRegistration1, prioritizedRegistration, nonPrioritizedRegistration2).shuffleAndPrioritize()

        registrations.size shouldBe 3
        registrations[0] shouldBe prioritizedRegistration
    }

    @Test
    fun `getBestResult when a result has more taken reservations`() {
        val a = LotteryResult(reservations = listOf(week1SiteA.copy(registration = defaultRegistration), week1SiteB.copy(registration = defaultRegistration)))
        val b = LotteryResult(reservations = listOf(week1SiteA.copy(registration = defaultRegistration)))
        LotteryService().getBestResult(a, b) shouldBe a
    }

    @Test
    fun `findAvailableReservation match based on preference`() {
        val availableReservations = listOf(week2SiteA, week1SiteB)

        val registration = Registration(name = "a", preferredSites= listOf(siteA, siteB), preferredDates = listOf(week1, week2), preferSiteOverDate= true)
        LotteryService().findAvailableReservation(availableReservations, registration) shouldBe week2SiteA

        val registration2 = registration.copy(preferSiteOverDate = false)
        LotteryService().findAvailableReservation(availableReservations, registration2) shouldBe week1SiteB
    }

    @Test
    fun `findAvailableReservation no match`() {
        val availableReservations = listOf(week1SiteA)

        val registration = Registration(preferredSites = listOf(siteB), preferredDates = listOf(week2), name = "a", preferSiteOverDate = true)

        LotteryService().findAvailableReservation(availableReservations, registration) shouldBe null
    }

    @Test
    fun `lottery should result in site A remaining for a single week because not enough people wanted it`() {
        val availableReservations = listOf(week1SiteA, week1SiteB, week2SiteA, week2SiteB)
        val reg1 = Registration(preferredSites = listOf(siteB), preferredDates = listOf(week1, week2), name = "reg1", preferSiteOverDate = true)
        val reg2 = Registration(preferredSites = listOf(siteB), preferredDates = listOf(week1, week2), name = "reg1", preferSiteOverDate = true)
        val reg3 = Registration(preferredSites = listOf(siteA), preferredDates = listOf(week1, week2), name = "reg1", preferSiteOverDate = true)

        val lotteryResult = LotteryService().run(availableReservations, listOf(reg1, reg2, reg3))

        lotteryResult.getAvailableReservations().size shouldBe 1
        lotteryResult.reservations[0].site shouldBe siteA
        lotteryResult.getTakenReservations().size shouldBe 3
    }

    @Test
    fun `the best lottery scenario is having one site left, with either registration 2 or 3 being unmatched`() {
        val availableReservations = listOf(week1SiteA, week1SiteB)
        val reg1 = Registration(preferredSites = listOf(siteA, siteB), preferredDates = listOf(week1), name = "reg1", preferSiteOverDate = true)
        val reg2 = Registration(preferredSites = listOf(siteA), preferredDates = listOf(week1), name = "reg2", preferSiteOverDate = true)
        val reg3 = Registration(preferredSites = listOf(siteA), preferredDates = listOf(week1), name = "reg3", preferSiteOverDate = true)

        for (i in 1..10) {
            val lotteryResult = LotteryService().run(availableReservations, listOf(reg1, reg2, reg3))

            lotteryResult.getAvailableReservations().size shouldBe 0
            lotteryResult.getTakenReservations().size shouldBe 2
            lotteryResult.unmatchedRegistrations.size shouldBe 1
            lotteryResult.unmatchedRegistrations[0] in listOf(reg2, reg3)

            // no registrations marked as both taken and unmatched
            !lotteryResult.getTakenReservations().any { takenReservation -> takenReservation.registration?.name in lotteryResult.unmatchedRegistrations.map { it.name } }

            // no duplicate reservations
            val reservationsByName = lotteryResult.getTakenReservations().groupBy { it.registration?.name }
            reservationsByName.values.all { it.size == 1 }
        }
   }

    @Test
    fun `reg3 is always unmatched because they're picky and don't have priority`() {
        val availableReservations = listOf(week1SiteA, week1SiteB)
        val reg1 = Registration(preferredSites = listOf(siteA, siteB), preferredDates = listOf(week1), name = "reg1", preferSiteOverDate = true)
        val reg2 = Registration(priorityRank = 1, preferredSites = listOf(siteA), preferredDates = listOf(week1), name = "reg2", preferSiteOverDate = true)
        val reg3 = Registration(preferredSites = listOf(siteA), preferredDates = listOf(week1), name = "reg3", preferSiteOverDate = true)

        for (i in 1..10) {
            val lotteryResult = LotteryService().run(availableReservations, listOf(reg1, reg2, reg3))

            lotteryResult.unmatchedRegistrations[0] shouldBe reg3
        }
    }
}
