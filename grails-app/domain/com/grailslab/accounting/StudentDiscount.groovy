package com.grailslab.accounting

import com.grailslab.gschoolcore.BasePersistentObj
import com.grailslab.stmgmt.Student

class StudentDiscount extends BasePersistentObj{

    Student student
    Double discountPercent
    FeeItems feeItems

    static constraints = {
    }
}
