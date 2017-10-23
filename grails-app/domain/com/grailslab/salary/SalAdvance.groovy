package com.grailslab.salary

import com.grailslab.enums.PayType
import com.grailslab.gschoolcore.BasePersistentObj
import com.grailslab.hr.Employee

class SalAdvance extends BasePersistentObj{
    Employee employee
    Date advanceDate
    Date paidDate
    Double outStandingAmount = 0
    Double currAmount
    Double installmentAmount
    String description
    PayType payType

    static constraints = {
        description nullable: true
        paidDate nullable: true
    }
}
