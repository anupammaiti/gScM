package com.grailslab.salary

import com.grailslab.enums.YearMonths
import com.grailslab.gschoolcore.BasePersistentObj
import com.grailslab.hr.Employee

class SalArea extends BasePersistentObj{

    YearMonths yearMonths
    Employee employee
    Double amount

    static constraints = {
    }
}
