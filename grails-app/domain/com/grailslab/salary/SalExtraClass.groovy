package com.grailslab.salary

import com.grailslab.enums.YearMonths
import com.grailslab.hr.Employee
import com.grailslab.gschoolcore.BasePersistentObj

class SalExtraClass extends BasePersistentObj {

    static transients = ['amount']
    YearMonths yearMonths
    Employee employee
    int numOfClass
    Double rate
    Double amount

    static constraints = {
        amount nullable: true

    }
    Double getAmount(){
        return this.numOfClass * this.rate
    }
}
