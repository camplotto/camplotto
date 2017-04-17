package camplotto

import camplotto.domain.LotteryResult
import camplotto.domain.Registration
import camplotto.domain.Reservation
import camplotto.excel.LotteryResultExcelWriter
import camplotto.service.LotteryService
import camplotto.excel.SeasonExcelParser


class ExcelRunner {
    static void main(String[] args) {
        if (!args || args.length != 2) {
            println "Usage: ExcelRunner <season-file-path> <lottery-result-file-path"
        }

        File seasonFile = new File(args[0])
        if (!seasonFile.exists()) {
            println "Can't find season file: ${seasonFile}"
            System.exit(1)
        }
        LotteryResult lotteryResult = computeResult(seasonFile)

        File resultFile = new File(args[1])
        LotteryResultExcelWriter.write(resultFile, lotteryResult)
    }

    static LotteryResult computeResult(File seasonFile) {
        SeasonExcelParser parser = new SeasonExcelParser(seasonFile)

        List<Reservation> availableReservations = parser.parseAvailableReservations()
        List<Registration> registrations = parser.parseRegistrations()

        return LotteryService.run(availableReservations, registrations)
    }
}
