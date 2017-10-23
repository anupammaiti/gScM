package com.grailslab.accounting

import com.grailslab.enums.InvoiceDayType
import com.grailslab.gschoolcore.BasePersistentObj

class InvoiceDay extends BasePersistentObj{
    Date invoiceDate
    InvoiceDayType invoiceDayType = InvoiceDayType.OPEN

    static constraints = {
    }
    static mapping = {
        sort invoiceDate: "desc" // or "desc"
    }
}
