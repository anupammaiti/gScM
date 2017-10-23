package com.grailslab.accounting

import com.grailslab.gschoolcore.BasePersistentObj


class ItemsDetail extends BasePersistentObj{
    String name
    Double amount =0
    Double discount =0     //in percentage
    Double netPayable =0
    Integer sortPosition
    Integer continuityOrder         //included to identify next available month in fee item list for monthly activated item

    Boolean quickFee1=Boolean.FALSE
    Boolean quickFee2=Boolean.FALSE
    Boolean quickFee3=Boolean.FALSE      // online fee
    static belongsTo = [feeItems:FeeItems]
    static constraints = {
        sortPosition nullable: true
        continuityOrder nullable: true
        quickFee3 nullable: true
    }
    static mapping = {
        sort continuityOrder: "asc"
    }
}
