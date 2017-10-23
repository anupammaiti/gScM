package com.grailslab.salary

import com.grailslab.enums.YearMonths
import com.grailslab.gschoolcore.BasePersistentObj
import com.grailslab.hr.Employee

class SalSetup extends BasePersistentObj{

    Employee employee
    Double grossSalary
    Double basic
    Double houseRent
    Double medical
    Double inCharge
    Double mobileAllowance
    Double others
    //Double area
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
        //area nullable: true
        adStatus nullable: true
        adsAmount nullable: true

    }
}
