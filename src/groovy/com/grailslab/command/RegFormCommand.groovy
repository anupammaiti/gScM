package com.grailslab.command

import com.grailslab.enums.BloodGroup
import com.grailslab.enums.Gender
import com.grailslab.enums.Religion
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.settings.ClassName

/**
 * Created by Hasnat on 6/28/2015.
 */
@grails.validation.Validateable
class RegFormCommand {
    Long regFormId
    String schoolClassIds
    Date regOpenDate
    Date regCloseDate
    Date fromBirthDate
    Date toBirthDate
    String instruction
    String applyType  //online, offline, both
    Double formPrice
    Integer serialStartFrom
    AcademicYear academicYear

    static constraints = {
        academicYear nullable: true
        instruction nullable: true
        formPrice nullable: true
        serialStartFrom nullable: true
        regFormId nullable:true
        fromBirthDate nullable: true
        toBirthDate nullable: true
    }
}
