package camplotto.excel

import camplotto.Registration
import camplotto.Reservation
import camplotto.ReservationDate
import camplotto.Site
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.FileInputStream

class ExcelReader {

    fun parseAvailableReservations(fileInputStream: FileInputStream): List<Reservation> {
        val availableReservations = mutableListOf<Reservation>()
        val columnHeaderNames = mutableMapOf<Int, String>()

        val wb = WorkbookFactory.create(fileInputStream)
        val sheet = wb.getSheet("Available Reservations")
        for (row in sheet) {
            try {
                val siteName = row.getCell(0).stringCellValue
                for (cell in row) {
                    // header
                    if (row.rowNum == 0 && !cell.stringCellValue.isNullOrEmpty()) {
                        columnHeaderNames[cell.columnIndex] = cell.stringCellValue
                    } else {
                        if (cell.columnIndex > 0 && columnHeaderNames.containsKey(cell.columnIndex)) {
                            if (cell.stringCellValue.isEmpty()) {
                                availableReservations.add(Reservation(
                                        reservationDate = ReservationDate(columnHeaderNames.getValue(cell.columnIndex)),
                                        site = Site(name = siteName)))
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                println("Exception reading line ${row.rowNum + 1}")
                throw e
            }
        }
        return availableReservations
    }

    fun parseRegistrations(fileInputStream: FileInputStream): List<Registration> {
        val registrations = mutableListOf<Registration>()

        val wb = WorkbookFactory.create(fileInputStream)
        val sheet = wb.getSheet("Registrations")
        for (row in sheet) {
            if (row.rowNum > 0 && row.getCell(0).stringCellValue.isNotEmpty()) {
                try {
                    registrations.add(Registration(
                            name = row.getCell(0).stringCellValue,
                            group = if (row.getCell(1).stringCellValue.isNotEmpty()) row.getCell(1).stringCellValue else null,
                            preferSiteOverDate = row.getCell(2).stringCellValue == "Site",
                            priorityRank = if (row.getCell(3).stringCellValue.isNotEmpty()) row.getCell(3).stringCellValue.toInt() else Int.MAX_VALUE,
                            preferredSites = row.getCell(4).stringCellValue.split(delimiters = *arrayOf(",")).map { Site(name = it) },
                            preferredDates = row.getCell(4).stringCellValue.split(delimiters = *arrayOf(",")).map { ReservationDate(name = it) })
                    )
                } catch (e: Exception) {
                    println("Exception reading line ${row.rowNum + 1}")
                    throw e
                }
            }
        }
        return registrations
    }

}
