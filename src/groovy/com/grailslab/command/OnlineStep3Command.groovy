package com.grailslab.command

import com.grailslab.enums.BloodGroup
import com.grailslab.enums.Gender
import com.grailslab.enums.Religion
import com.grailslab.settings.Profession

/**
 * Created by Hasnat on 6/28/2015.
 */
@grails.validation.Validateable
class OnlineStep3Command {
    Long regId
    String fathersName
    String fathersProfession
    String fathersMobile
    Double fathersIncome
    Boolean fathersIsalive
    String presentAddress
    String permanentAddress


    String mothersName
    String mothersProfession
    String mothersMobile
    Double mothersIncome
    String mothersIsalive

    String legalGuardianName
    String legalGuardianProfession
    String legalGuardianMobile
    String relationWithLegalGuardian

    static constraints = {
        fathersIsalive nullable:true
        mothersMobile nullable: true
        mothersIncome nullable: true
        mothersIsalive nullable: true
        legalGuardianName nullable: true
        legalGuardianProfession nullable: true
        legalGuardianMobile nullable: true
        relationWithLegalGuardian nullable: true


    }
}
