package camplotto.excel

import camplotto.domain.Registration
import camplotto.domain.Reservation
import spock.lang.Specification

class SeasonExcelParserSpec extends Specification {

    def "ParseAvailableReservations"() {
        given:
        File testSeasonFile = new File(this.class.getResource("/sample-season.xlsx").toURI())
        SeasonExcelParser parser = new SeasonExcelParser(testSeasonFile)

        when:
        List<Reservation> availableReservations = parser.parseAvailableReservations()

        then:
        availableReservations
        availableReservations.size() == 8
        availableReservations.findAll { it.site.name == "Site A" }.size() == 3
        availableReservations.find { it.site.name == "Site A" && it.reservationDate.name == "Week 1" }
        availableReservations.findAll { it.site.name == "Site B" }.size() == 2
        availableReservations.findAll { it.site.name == "Site C" }.size() == 3
    }

    def "ParseRegistrations"() {
        given:
        File testSeasonFile = new File(this.class.getResource("/sample-season.xlsx").toURI())
        SeasonExcelParser parser = new SeasonExcelParser(testSeasonFile)

        when:
        List<Registration> registrations = parser.parseRegistrations()

        then:
        registrations
        registrations.size() == 10
        registrations.find { it.name == "Family A" }.hasPriority

        registrations.find { it.name == "Family A" }.preferredSites.size() == 5
        registrations.find { it.name == "Family A" }.preferredSites[0].name == "Site A"

        registrations.find { it.name == "Family A" }.preferredDates.size() == 5
        registrations.find { it.name == "Family A" }.preferredDates[0].name == "Week 1"

        registrations.find { it.name == "Family B" }.preferSiteOverDate
    }
}
