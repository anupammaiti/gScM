package com.grailslab.accounting

import com.grailslab.gschoolcore.BasePersistentObj


class Assets extends BasePersistentObj{

    ChartOfAccount account
    String name
    Long invoiceId
    Long referenceId
    Date buyDate
    Double cost
    String comment
    Double depreciation
    Date lastDepreciation
    Double currentValue


    static constraints = {
        invoiceId nullable: true
        referenceId nullable: true
        buyDate nullable: true
        cost nullable: true
        comment nullable: true
        depreciation nullable: true
        lastDepreciation nullable: true
        currentValue nullable: true
    }
}
