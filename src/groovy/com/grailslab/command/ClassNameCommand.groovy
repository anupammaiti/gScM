package com.grailslab.command

import com.grailslab.hr.HrPeriod

//  ClassNameController - save
@grails.validation.Validateable
class ClassNameCommand {
    Long id
    String name
    Integer sortPosition
    boolean allowOptionalSubject
    boolean groupsAvailable
    Boolean isCollegeSection
    Integer expectedNumber
    Integer workingDays
    HrPeriod hrPeriod

    static constraints = {
        id nullable: true
        workingDays nullable: true
        isCollegeSection nullable: true
        hrPeriod nullable: true

    }
}
