package camplotto

import camplotto.domain.LotteryResult
import camplotto.domain.Registration
import camplotto.domain.Reservation
import camplotto.service.LotteryService
import camplotto.excel.SeasonExcelParser
import spock.lang.Specification

class ExcelRunnerSpec extends Specification {

    def "ComputeResult"() {
        given:
        File testSeasonFile = new File(this.class.getResource("/sample-season.xlsx").toURI())
        SeasonExcelParser parser = new SeasonExcelParser(testSeasonFile)

        when:
        List<Reservation> availableReservations = parser.parseAvailableReservations()
        List<Registration> registrations = parser.parseRegistrations()

        LotteryResult result = LotteryService.run(availableReservations, registrations)

        then: "All available spots should be taken, with a couple unmatched registrations"
        result.availableReservations.size() == 0
        result.takenReservations.size() == 8
        result.unmatchedRegistrations.size() == 2

        and: "Family A should always get a spot because they have priority"
        result.takenReservations.find { Reservation reservation -> reservation.registration.name == "Family A" }
    }


}
