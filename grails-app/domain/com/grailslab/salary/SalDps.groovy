package com.grailslab.salary

import com.grailslab.gschoolcore.BasePersistentObj
import com.grailslab.hr.Employee

class SalDps extends BasePersistentObj{
    Employee employee
    Double openInsAmount = 0
    Double openOwnAmount = 0
    Double totalInsAmount = 0
    Double totalOwnAmount = 0
    String description

    static constraints = {
        description nullable: true
    }
}
