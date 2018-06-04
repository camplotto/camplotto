package camplotto

class LotteryService {

    private val singleRegistrationService = SingleRegistrationService()
    private val groupRegistrationService = GroupRegistrationService()

    fun run(availableReservations: List<Reservation>, registrations: List<Registration>, numSimulations: Int = 1000): LotteryResult {

        var bestResult = LotteryResult()

        for (i in 1..numSimulations) {
            val lotteryResult = LotteryResult(reservations = availableReservations.map { it.copy() }, unmatchedRegistrations = mutableListOf())

            val currentRegistrations = registrations.map { it.copy() }.shuffleAndPrioritize()

            for (registration in currentRegistrations) {
                if (registration.wasGroupProcessed) {
                    continue
                }

                if (registration.isPartOfGroup()) {
                    val registrationsInGroup = registrations.getGroupMembers(registration)
                    groupRegistrationService.handleRegistration(lotteryResult, registrationsInGroup)
                } else {
                    singleRegistrationService.handleRegistration(lotteryResult, registration)
                }
            }

            bestResult = getBestLotteryResult(lotteryResult, bestResult)
        }

        return bestResult
    }

    fun getBestLotteryResult(a: LotteryResult, b: LotteryResult): LotteryResult {
        return if (a.getTakenReservations().size > b.getTakenReservations().size) a else b
    }
}
