package com.grailslab.salary

import com.grailslab.enums.PayType
import com.grailslab.gschoolcore.BasePersistentObj
import com.grailslab.hr.Employee

class SalPc extends BasePersistentObj {
    Employee employee
    Date paidDate
    Double outStandingAmount = 0
    Double payableAmount = 0
    Double installmentAmount = 0
    String description
    PayType payType

    static constraints = {
        description nullable: true
        paidDate nullable: true
    }
}
