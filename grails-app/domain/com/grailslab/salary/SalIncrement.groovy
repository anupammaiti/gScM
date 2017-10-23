package com.grailslab.salary

import com.grailslab.enums.YearMonths
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.gschoolcore.BasePersistentObj
import com.grailslab.hr.Employee

class SalIncrement extends BasePersistentObj{
    Employee employee
    Double grossSalary
    Double basic
    Double houseRent
    Double medical
    Double inCharge
    Double others
    YearMonths yearMonths
    String incrementStatus

    static constraints = {
        grossSalary nullable: true
        basic nullable: true
        houseRent nullable: true
        medical nullable: true
        inCharge nullable: true
        others nullable: true
        incrementStatus nullable: true

    }

}
