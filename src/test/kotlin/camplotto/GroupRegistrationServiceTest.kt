package camplotto

import io.kotlintest.matchers.collections.shouldContainAll
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test


class GroupRegistrationServiceTest {

    private val siteA = Site(name = "a")
    private val siteB = Site(name = "b")

    private val week1 = ReservationDate(name = "week 1")
    private val week2 = ReservationDate(name = "week 2")

    private val service = GroupRegistrationService()

    @Test
    fun getGroupSiteCombinations() {
        val registration1 = Registration(name = "registration1", group = "groupA", preferredSites = listOf(siteA, siteB), preferredDates = listOf(week1, week2), preferSiteOverDate = false)
        val registration2 = Registration(name = "registration2", group = "groupA", preferredSites = listOf(siteA, siteB), preferredDates = listOf(week1, week2), preferSiteOverDate = false)

        val combinations = service.getGroupSiteCombinations(listOf(registration1, registration2))
        combinations.size shouldBe 2
        combinations.shouldContainAll(listOf(siteA, siteB), listOf(siteB, siteA))
    }

}
