package com.grailslab.accounting

import com.grailslab.gschoolcore.BasePersistentObj
import com.grailslab.stmgmt.Student

class InvoiceDetails extends BasePersistentObj{
    Invoice invoice
    Student student
    FeeItems feeItems
    String description      // in case of item details. ie. January - March
    Integer quantity =0
    Double amount =0
    Double discount =0     //discount percent
    Double netPayment =0
    Double totalPayment =0

    static constraints = {
        student nullable: true
        description nullable: true
    }
}
