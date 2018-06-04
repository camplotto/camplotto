package camplotto

import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test


class SingleRegistrationServiceTest {

    private val siteA = Site(name = "a")
    private val siteB = Site(name = "b")

    private val week1 = ReservationDate(name = "week 1")
    private val week2 = ReservationDate(name = "week 2")

    private val week1SiteA = Reservation(reservationDate = week1, site = siteA)
    private val week2SiteA = Reservation(reservationDate = week2, site = siteA)
    private val week1SiteB = Reservation(reservationDate = week1, site = siteB)

    private val service = SingleRegistrationService()

    @Test
    fun `findAvailableReservation match based on preference`() {
        val availableReservations = listOf(week2SiteA, week1SiteB)

        val registration = Registration(name = "a", preferredSites= listOf(siteA, siteB), preferredDates = listOf(week1, week2), preferSiteOverDate= true)
        service.findAvailableSingleRegistration(availableReservations, registration) shouldBe week2SiteA

        val registration2 = registration.copy(preferSiteOverDate = false)
        service.findAvailableSingleRegistration(availableReservations, registration2) shouldBe week1SiteB
    }

    @Test
    fun `findAvailableReservation no match`() {
        val availableReservations = listOf(week1SiteA)

        val registration = Registration(preferredSites = listOf(siteB), preferredDates = listOf(week2), name = "a", preferSiteOverDate = true)

        service.findAvailableSingleRegistration(availableReservations, registration) shouldBe null
    }

}
