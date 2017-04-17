package camplotto.excel

import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellReference

/**
 * Groovy parser for Microsoft Excel spreadsheets.
 * Based on @author Goran Ehrsson's post: http://www.technipelago.se/content/technipelago/blog/44
 */
class ExcelReader {

    Workbook workbook
    def labels
    Row row
    DataFormatter dataFormatter

    ExcelReader(InputStream inputStream) {
        dataFormatter = new DataFormatter()

        Row.metaClass.getAt = { int idx ->
            Cell cell = delegate.getCell(idx)

            if (!cell) {
                return null
            }

            return dataFormatter.formatCellValue(cell)
        }

        Row.metaClass.getDisplayRowNum = { ->
            delegate.getRowNum() + 1
        }

        workbook = WorkbookFactory.create(inputStream)
    }

    Sheet getSheet(idx) {
        def sheet
        if (!idx) idx = 0
        if (idx instanceof Number) {
            sheet = workbook.getSheetAt(idx)
        } else if (idx ==~ /^\d+$/) {
            sheet = workbook.getSheetAt(Integer.valueOf(idx))
        } else {
            sheet = workbook.getSheet(idx.toString())
        }
        return sheet
    }

    // use this when you're iterating the rows using eachLine. zero-based.  e.g. Send 0 For row 1 as seen by a human
    String cell(idx) {
        if (labels && (idx instanceof String)) {
            String idxName = idx
            idx = labels.indexOf(idx.toLowerCase())
            if (idx < 0) {
                throw new IllegalStateException("can't find cell named $idxName")
            }
        }

        return row[idx]?.trim()
    }

    // use this when you want direct access to a cell in the grid. e.g. B12 For column B and row 12 as seen by a human.
    String cellByReference(String cellReferenceString) {
        CellReference cellReference = new CellReference(cellReferenceString)
        Row r = getSheet(0).getRow(cellReference.getRow())
        if (r != null) {
            Cell cell = r.getCell(cellReference.getCol())
            return dataFormatter.formatCellValue(cell)?.trim()
        }
        return null
    }

    // indicates if the row is empty.  current implementation just looks at the first couple columns for better efficiency
    boolean isRowEmpty(Row row) {
        List<Integer> columnIndexes = [0, 1]
        return !columnIndexes.any { row[it] }
    }

    def propertyMissing(String name) {
        cell(name)
    }

    def eachLine(Map params = [:], Closure closure) {
        int offset = params.offset ?: 0
        long max = params.max ?: 9999999
        Sheet sheet = getSheet(params.sheet)
        Iterator rowIterator = sheet.rowIterator()
        int linesRead = 0

        if (params.labels) {
            labels = rowIterator.next().collect { it.toString().toLowerCase() }
        }
        offset.times { rowIterator.next() }

        closure.setDelegate(this)
        closure.resolveStrategy = Closure.DELEGATE_FIRST

        while (rowIterator.hasNext() && linesRead++ < max) {
            row = rowIterator.next()
            closure.call(row, linesRead)
        }
    }


}

