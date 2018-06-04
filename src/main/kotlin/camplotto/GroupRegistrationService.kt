package camplotto

import org.paukov.combinatorics3.Generator

class GroupRegistrationService {

    fun handleRegistration(currentResult: LotteryResult, registrationsInGroup: List<Registration>) {
        registrationsInGroup.forEach { it.wasGroupProcessed = true }

        val availableReservations = currentResult.getAvailableReservations()

        val commonPreferredDates: List<ReservationDate> = getCommonPreferredDates(registrationsInGroup)

        val groupSiteCombinations: Iterable<List<Site>> = getGroupSiteCombinations(registrationsInGroup)

        for (date in commonPreferredDates) {
            for (groupSites in groupSiteCombinations) {
                val availableReservationsForDate = availableReservations.filter { it.reservationDate == date }

                if (availableReservationsForDate.map { it.site }.containsAll(groupSites)) {
                    // if we're here, we found an available site for everyone in the group for the given date
                    groupSites.forEachIndexed { index, chosenSite ->
                        val registration = registrationsInGroup[index]
                        val reservation = availableReservationsForDate.find { it.site == chosenSite }
                                ?: throw IllegalStateException("Expected to find matching site for site $chosenSite and date $date")
                        reservation.registration = registration
                    }
                    return
                }
            }
        }

        // if we got here, we couldn't find a site for everyone in the group, so no one in the group gets in
        currentResult.unmatchedRegistrations.addAll(registrationsInGroup)
    }

    /**
     * Gets the union of all the preferred dates for the group. i.e. It eliminates any dates chosen by one part in the group that aren't
     * also selected by all other group members, to avoid situations where one group accidentally picks a date that doesn't work for others in the group.
     */
    fun getCommonPreferredDates(registrationsInGroup: List<Registration>) : List<ReservationDate> {
        return registrationsInGroup
                .flatMap { it.preferredDates }
                .distinct()
                .filter { reservationDate -> registrationsInGroup.all { registration -> registration.preferredDates.contains(reservationDate) } }
    }

    /**
     * For the registrations in the group, returns all valid permutations of sites that would work for the entire group.
     * e.g. If two group registrants prefer Site1 or Site 2, then the valid possible options are
     * [ [Site 1, Site 2], [Site2, Site1] ]
     * This excludes invalid site options like [ [Site1, Site1], [Site2, Site2] ]
     */
    fun getGroupSiteCombinations(registrationsInGroup: List<Registration>): List<List<Site>> {
        val allSiteCombinations = Generator.cartesianProduct(*registrationsInGroup.map { it.preferredSites }.toTypedArray())
        // todo figure out why ^ already eliminates duplicate combos like [Site1, Site1]. i.e. why isn't the below stuff needed?
        return allSiteCombinations
                .map { siteCombination -> siteCombination.distinctBy { it.name }}
                .filter { it.size == registrationsInGroup.size }
    }

}
