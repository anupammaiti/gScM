package com.grailslab.festival

import com.grailslab.gschoolcore.BasePersistentObj

class FesProgram extends BasePersistentObj{
    String name
    Date startDate
    Date endDate
    String olympiadTopics        //comma separated OlympiadType keys
    String helpContact
    Date regOpenDate
    Date regCloseDate

    static constraints = {
        olympiadTopics nullable: true
        helpContact nullable: true
        regOpenDate nullable: true
        regCloseDate nullable: true
    }
}
