package camplotto.excel

import camplotto.domain.LotteryResult
import net.sf.jxls.transformer.XLSTransformer

class LotteryResultExcelWriter {

    static void write(File outputTemplateFile, File outputFile, LotteryResult lotteryResult) {
        String templateFilePath = outputTemplateFile.absolutePath

        Map beans = [
                takenReservations     : lotteryResult.takenReservations,
                availableReservations : lotteryResult.availableReservations,
                unmatchedRegistrations: lotteryResult.unmatchedRegistrations,
        ]
        XLSTransformer transformer = new XLSTransformer()
        transformer.transformXLS(templateFilePath, beans, outputFile.absolutePath)
    }
}
