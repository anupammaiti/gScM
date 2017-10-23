package com.grailslab.accounting

import com.grailslab.gschoolcore.BasePersistentObj
import com.grailslab.stmgmt.Student

class FeePayments extends BasePersistentObj {
    Student student
    FeeItems feeItems
    ItemsDetail itemsDetail
    String itemDetailName
    Integer itemDetailSortPosition
    Integer quantity =0
    Double amount =0
    Double discount =0     //discount percent
    Double netPayment =0
    Double totalPayment =0
    Invoice invoice
    Boolean includedInSummary = false
    Date summaryDate

    static constraints = {
        itemsDetail nullable: true
        itemDetailName nullable: true
        itemDetailSortPosition nullable: true
        includedInSummary nullable: true
        summaryDate nullable: true
    }
    static mapping = {
        includedInSummary defaultValue: Boolean.FALSE
    }
}
