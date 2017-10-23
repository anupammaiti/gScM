package com.grailslab.command

import com.grailslab.enums.SalaryStatus
import com.grailslab.enums.YearMonths
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.hr.Employee

/**
 * Created by USER on 31-May-17.
 */
@grails.validation.Validateable
class SalaryBonusCommand {
    Long id
    String festivalName
    Date joinBefore
    AcademicYear academicYear
    String basedOn
    Double bonusPercentage
    static constraints = {

        id nullable: true
    }
}
