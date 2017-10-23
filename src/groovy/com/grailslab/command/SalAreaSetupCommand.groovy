package com.grailslab.command

import com.grailslab.enums.YearMonths
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.hr.Employee

/**
 * Created by bddev on 12/3/2016.
 */
@grails.validation.Validateable
class SalAreaSetupCommand {
    AcademicYear academicYear
    YearMonths yearMonths
    Employee employee
    Double amount

}
