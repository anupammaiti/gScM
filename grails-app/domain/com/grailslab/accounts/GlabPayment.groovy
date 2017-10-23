package com.grailslab.accounts

import com.grailslab.gschoolcore.BasePersistentObj

class GlabPayment extends BasePersistentObj{
    String invoiceNo
    Date invoiceDate
    Date dueDate
    String sortDesc

    String accountNo
    String terms
    String notes

    String paymentStatus        //draft, submit,pending, paid
    String paymentMode          //bank, cash, bkash
    Double invoiceAmount
    Double paymentAmount
    Date paymentDate
    String payNote



    static constraints = {
        invoiceNo nullable: true
        sortDesc nullable: true
        invoiceDate nullable: true
        dueDate nullable: true
        paymentStatus nullable: true
        paymentDate nullable: true
        accountNo nullable: true
        terms nullable: true
        notes nullable: true
        paymentMode nullable: true
        payNote nullable: true
        paymentAmount nullable: true
        invoiceAmount nullable: true

    }
}
