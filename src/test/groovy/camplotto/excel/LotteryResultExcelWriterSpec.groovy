package camplotto.excel

import camplotto.domain.*
import spock.lang.Specification

class LotteryResultExcelWriterSpec extends Specification {
    def "Write"() {
        given:
        LotteryResult lotteryResult = new LotteryResult()

        Reservation takenReservation = new Reservation(
                registration: new Registration(name: "Registration A"),
                site: new Site(name: "Site 1"),
                reservationDate: new ReservationDate(name: "Week 1"))
        lotteryResult.takenReservations = [takenReservation]

        Reservation availableReservation = new Reservation(
                site: new Site(name: "Site 2"),
                reservationDate: new ReservationDate(name: "Week 2")
        )
        lotteryResult.availableReservations = [availableReservation]

        Registration unmatchedRegistration = new Registration(name: "Registration B")
        lotteryResult.unmatchedRegistrations = [unmatchedRegistration]

        when:
        LotteryResultExcelWriter.write(new File("/tmp/lottery-result.xlsx"), lotteryResult)

        then:
        true
    }
}
