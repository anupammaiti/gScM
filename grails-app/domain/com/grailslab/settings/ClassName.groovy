package com.grailslab.settings

import com.grailslab.gschoolcore.BasePersistentObj
import com.grailslab.hr.HrPeriod


class ClassName extends BasePersistentObj{
    String name
    Integer sortPosition
    Boolean allowOptionalSubject = false
    Boolean groupsAvailable
    Boolean isCollegeSection  = false
    Boolean subjectGroup  = false
    Boolean subjectSba  = false
    Integer workingDays
    Integer expectedNumber
    HrPeriod hrPeriod
    //Per Subject in Percentage. Will use for Progress report card week subjects.
    static constraints = {
        groupsAvailable nullable: true
        workingDays nullable: true
        isCollegeSection nullable: true
        subjectGroup nullable: true
        subjectSba nullable: true
        hrPeriod nullable: true
    }
    static mapping = {
        expectedNumber defaultValue: 60
        groupsAvailable defaultValue: Boolean.FALSE
    }
}
