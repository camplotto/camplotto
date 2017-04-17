package camplotto.excel

import camplotto.domain.Registration
import camplotto.domain.Reservation
import camplotto.domain.ReservationDate
import camplotto.domain.Site
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row

class SeasonExcelParser {

    File seasonExcelFile

    SeasonExcelParser(File seasonExcelFile) {
        this.seasonExcelFile = seasonExcelFile
    }

    List<Reservation> parseAvailableReservations() {
        List<Reservation> availableReservations = []
        List<String> reservationDateNames = []

        InputStream fileInputStream = seasonExcelFile.newInputStream()
        ExcelReader excelReader = new ExcelReader(fileInputStream)
        Map options = [sheet: "Available Reservations"]
        excelReader.eachLine(options) { Row row, int lineNumber ->
            if (lineNumber == 1) {
                row.eachWithIndex { Cell cell, int columnIndex ->
                    reservationDateNames << cell.stringCellValue?.trim()
                }
            } else if (!isRowEmpty(row)) {
                Site site = new Site()

                row.eachWithIndex { Cell cell, int columnIndex ->
                    if (columnIndex == 0) {
                        site.name = cell.stringCellValue?.trim()
                    } else if (cell.stringCellValue) {
                        Reservation reservation = new Reservation(site: site)
                        reservation.reservationDate = new ReservationDate(name: reservationDateNames[columnIndex])
                        availableReservations << reservation
                    }
                }
            }
        }
        fileInputStream?.close()

        return availableReservations
    }

    List<Registration> parseRegistrations() {
        List<Registration> registrations = []

        InputStream fileInputStream = seasonExcelFile.newInputStream()
        ExcelReader excelReader = new ExcelReader(fileInputStream)
        Map options = [sheet: "Registrations", offset: 1]
        excelReader.eachLine(options) { Row row, int lineNumber ->
            if (!isRowEmpty(row)) {
                Registration registration = new Registration()
                registration.name = row.getCell(0).stringCellValue.trim()
                registration.preferSiteOverDate = row.getCell(1).stringCellValue?.trim()?.equalsIgnoreCase("site")
                registration.hasPriority = row.getCell(2).stringCellValue?.equalsIgnoreCase("y")
                registration.preferredSites = row.getCell(3).stringCellValue?.split(",")?.collect { new Site(name: it.trim()) }
                registration.preferredDates = row.getCell(4).stringCellValue?.split(",")?.collect { new ReservationDate(name: it.trim()) }
                registrations << registration
            }
        }

        fileInputStream?.close()

        return registrations
    }

}
