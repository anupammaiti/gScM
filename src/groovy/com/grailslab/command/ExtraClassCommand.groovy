package com.grailslab.command

import com.grailslab.enums.YearMonths
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.hr.Employee

/**
 * Created by Hasnat on 6/27/2015.
 */
@grails.validation.Validateable
class ExtraClassCommand {
    Employee employee
    YearMonths yearMonths
    AcademicYear academicYear
    Integer numOfClass
    Double rate

    static constraints = {

    }
}
