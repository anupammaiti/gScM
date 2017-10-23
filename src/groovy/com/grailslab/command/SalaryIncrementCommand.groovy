package com.grailslab.command


import com.grailslab.enums.YearMonths
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.hr.Employee

/**
 * Created by USER on 23-Mar-17.
 */
@grails.validation.Validateable
class SalaryIncrementCommand {

    Long id
    YearMonths yearMonths
    AcademicYear academicYear

    Employee employee
    Double basic
    Double houseRent
    Double medical
    Double grossSalary
    Double inCharge
    Double others


    static constraints = {

        id nullable: true
        basic nullable: true
        houseRent nullable: true
        medical nullable: true
        grossSalary nullable: true
        inCharge nullable: true
        others nullable: true
        academicYear nullable: false
        yearMonths nullable: false


    }

}
