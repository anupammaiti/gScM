package com.grailslab.accounting


import com.grailslab.enums.AccountType
import com.grailslab.enums.PaymentType
import com.grailslab.gschoolcore.BasePersistentObj
import com.grailslab.stmgmt.Student

class Invoice extends BasePersistentObj{
    Student student
    String invoiceNo
    Double amount = 0         //Total Paid amonut
    Double discount = 0     // Total Discount
    Double payment = 0      // Net Payment
    Date invoiceDate
    String description
    String comment
    AccountType accountType     // Income or expense
    Boolean verified = true
    Boolean paid = true
    String refInvoiceNo     //in case of edit invoice
    PaymentType paymentType = PaymentType.CASH
    String transactionId
    static constraints = {
        invoiceNo unique: true
        student nullable: true
        description nullable: true, maxSize: 2000
        comment nullable: true
        refInvoiceNo nullable: true
        paymentType nullable: true
        transactionId nullable: true
    }
}
