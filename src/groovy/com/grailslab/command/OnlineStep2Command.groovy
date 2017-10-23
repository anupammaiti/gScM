package com.grailslab.command

import com.grailslab.enums.BloodGroup
import com.grailslab.enums.Gender
import com.grailslab.enums.Religion
import com.grailslab.settings.ClassName
import com.grailslab.stmgmt.RegOnlineRegistration

/**
 * Created by Hasnat on 6/28/2015.
 */
@grails.validation.Validateable
class OnlineStep2Command {
    String nameBangla
    Long regId
    Religion religion
    Gender gender
    BloodGroup bloodGroup
    String birthCertificateNo
    String nationality
    String email
    Boolean hasBroOrSisInSchool
    String broOrSis1Id
    String broOrSis2Id

    static constraints = {
        email nullable: true
        bloodGroup nullable: true
        hasBroOrSisInSchool nullable: true
        broOrSis1Id nullable: true
        broOrSis2Id nullable: true
    }
}
