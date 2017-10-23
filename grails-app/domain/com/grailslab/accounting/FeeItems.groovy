package com.grailslab.accounting


import com.grailslab.enums.FeeItemType
import com.grailslab.enums.FeeType
import com.grailslab.enums.FeeAppliedType
import com.grailslab.enums.DueOnType
import com.grailslab.enums.FeeIterationType
import com.grailslab.gschoolcore.BasePersistentObj
import com.grailslab.settings.ClassName

class FeeItems extends BasePersistentObj {
    ChartOfAccount chartOfAccount

    Fees fees
    StationaryItem stationaryItem
    Cafeteria cafeteria

    String name
    String code
    Double amount =0
    Double discount =0     //in percentage
    Double netPayable =0

    FeeItemType feeItemType
    ClassName className
    FeeType feeType                     //compulsory, optional
    FeeAppliedType feeAppliedType       //all, class, section
    DueOnType dueOnType                 //SalaryAdvance, due, on delivery
    FeeIterationType iterationType      //On Receive, daily, weekly, monthly, yearly

    static hasMany = [itemsDetail: ItemsDetail]


    Boolean quickFee1=Boolean.FALSE
    Boolean quickFee2=Boolean.FALSE
    Boolean quickFee3=Boolean.FALSE  //Online fee
    static constraints = {
        fees nullable: true
        stationaryItem nullable: true
        cafeteria nullable: true
        className nullable: true
        quickFee3 nullable: true
    }

}
