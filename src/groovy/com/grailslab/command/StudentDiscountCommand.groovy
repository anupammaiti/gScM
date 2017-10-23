package com.grailslab.command

import com.grailslab.accounting.FeeItems
import com.grailslab.stmgmt.Student

/**
 * Created by Hasnat on 6/27/2015.
 */
@grails.validation.Validateable
class StudentDiscountCommand {
    Long id
    Student student
    Double discountPercent
    FeeItems feeItems

    static constraints = {
        id nullable: true
        student nullable: true
        feeItems nullable: true
    }
}
