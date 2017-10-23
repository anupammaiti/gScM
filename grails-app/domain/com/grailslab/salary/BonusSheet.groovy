package com.grailslab.salary

import com.grailslab.gschoolcore.BasePersistentObj
import com.grailslab.hr.Employee

class BonusSheet extends BasePersistentObj{
    static belongsTo = [bonusMaster:BonusMaster]
    Employee employee
    Double amount
    Double baseAmount

    static constraints = {
    }
}
