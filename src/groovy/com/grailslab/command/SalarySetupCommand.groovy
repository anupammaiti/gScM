package com.grailslab.command

import com.grailslab.enums.YearMonths
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.hr.Employee

/**
 * Created by Hasnat on 6/27/2015.
 */
@grails.validation.Validateable
class SalarySetupCommand {
    Employee employee
    Double grossSalary
    Double basic
    Double houseRent
    Double medical
    Double inCharge
    Double mobileAllowance
    Double others
    Double area
    Boolean pfStatus
    Boolean adStatus
    Double dpsAmount
    Double adsAmount
    Double fine
    Double pc
    Double netPayable

    static constraints = {
        grossSalary nullable: true
        basic nullable: true
        houseRent nullable: true
        medical nullable: true
        inCharge nullable: true
        mobileAllowance nullable: true
        others nullable: true
        pfStatus nullable: true
        dpsAmount nullable: true
        fine nullable: true
        pc nullable: true
        netPayable nullable: true
        area nullable: true
        adStatus nullable: true
        adsAmount nullable: true

    }
}
